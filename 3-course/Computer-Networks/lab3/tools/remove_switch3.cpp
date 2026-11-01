#include <QtCore/QDataStream>
#include <QtCore/QFile>
#include <QtCore/QPointF>
#include <QtCore/QString>

#include <array>
#include <vector>

struct Mac { std::array<quint8, 6> b; };
struct Ip { std::array<quint8, 4> b; };
struct Iface { Mac mac; Ip ip; Ip mask; QString name; quint64 st[4]{}; };
struct Route { Ip dest; Ip mask; Ip gw; int time; qint8 metric; Ip out; };
struct Program {
    int id = -1; bool enabled = true;
    int split = 0; bool trigger = true;
    int offerTime = 60;
    struct State { int xid, time; Ip server, lastIp; QString name; };
    std::vector<State> states;
    struct StaticRecord { Mac mac; Ip ip, mask, gw; int time; };
    std::vector<StaticRecord> staticRecords;
    QString iface; Ip begin{}, end{}, mask{}, gw{}; int lease = 1200; bool dynamic = true; int waiting = 60;
};
struct Dev {
    QPointF pos; int type; QString note; Mac mac{}; Ip ip{}; Ip mask{}; quint64 st[4]{};
    int ports = 0; int collision = 0; bool routing = false;
    std::vector<Iface> ifaces; std::vector<Route> routes; std::vector<Program> programs;
};
struct Cable { QPointF a, b; QString pa, pb; };
struct Label { QPointF p; QString text; };

static Ip ip(quint8 a, quint8 b, quint8 c, quint8 d) { return Ip{{a,b,c,d}}; }
static bool eq(const Ip &a, const Ip &b) { return a.b == b.b; }
static void rip(QDataStream &s, Ip &x) { for (auto &b : x.b) s >> b; }
static void wip(QDataStream &s, const Ip &x) { for (auto b : x.b) s << b; }
static void rmac(QDataStream &s, Mac &m) { for (auto &b : m.b) s >> b; }
static void wmac(QDataStream &s, const Mac &m) { for (auto b : m.b) s << b; }
static void rstats(QDataStream &s, quint64 st[4]) { for (int i=0;i<4;i++) s >> st[i]; }
static void wstats(QDataStream &s, const quint64 st[4]) { for (int i=0;i<4;i++) s << st[i]; }
static void rchip(QDataStream &s, Mac &m, Ip &addr, Ip &mask, quint64 st[4]) { rmac(s,m); rip(s,addr); rip(s,mask); rstats(s,st); }
static void wchip(QDataStream &s, const Mac &m, const Ip &addr, const Ip &mask, const quint64 st[4]) { wmac(s,m); wip(s,addr); wip(s,mask); wstats(s,st); }
static Route rroute(QDataStream &s) { Route r; rip(s,r.dest); rip(s,r.mask); rip(s,r.gw); s >> r.time >> r.metric; rip(s,r.out); return r; }
static void wroute(QDataStream &s, const Route &r) { wip(s,r.dest); wip(s,r.mask); wip(s,r.gw); s << r.time << r.metric; wip(s,r.out); }

static Program rprogram(QDataStream &s) {
    Program p; s >> p.id >> p.enabled;
    if (p.id == 0) {
        s >> p.split >> p.trigger;
    } else if (p.id == 1) {
        int states; s >> p.offerTime >> states;
        for (int i=0;i<states;i++) {
            Program::State st; s >> st.xid >> st.time; rip(s,st.server); rip(s,st.lastIp); s >> st.name; p.states.push_back(st);
        }
    } else if (p.id == 2) {
        int staticCount; s >> staticCount;
        for (int i=0;i<staticCount;i++) {
            Program::StaticRecord r; rmac(s,r.mac); rip(s,r.ip); rip(s,r.mask); rip(s,r.gw); s >> r.time; p.staticRecords.push_back(r);
        }
        s >> p.iface; rip(s,p.begin); rip(s,p.end); rip(s,p.mask); rip(s,p.gw);
        s >> p.lease >> p.dynamic >> p.waiting;
    }
    return p;
}

static void wprogram(QDataStream &s, const Program &p) {
    s << p.id << p.enabled;
    if (p.id == 0) {
        s << p.split << p.trigger;
    } else if (p.id == 1) {
        s << p.offerTime << int(p.states.size());
        for (const auto &st : p.states) { s << st.xid << st.time; wip(s,st.server); wip(s,st.lastIp); s << st.name; }
    } else if (p.id == 2) {
        s << int(p.staticRecords.size());
        for (const auto &r : p.staticRecords) { wmac(s,r.mac); wip(s,r.ip); wip(s,r.mask); wip(s,r.gw); s << r.time; }
        s << p.iface; wip(s,p.begin); wip(s,p.end); wip(s,p.mask); wip(s,p.gw);
        s << p.lease << p.dynamic << p.waiting;
    }
}

static Dev rdev(QDataStream &s) {
    Dev d; s >> d.pos >> d.type >> d.note;
    if (d.type == 4 || d.type == 5) {
        rchip(s,d.mac,d.ip,d.mask,d.st); s >> d.ports; if (d.type == 4) s >> d.collision; return d;
    }
    int ifaces; s >> ifaces;
    for (int i=0;i<ifaces;i++) { Iface f; rchip(s,f.mac,f.ip,f.mask,f.st); s >> f.name; d.ifaces.push_back(f); }
    int routes, programs; s >> d.routing >> routes;
    for (int i=0;i<routes;i++) d.routes.push_back(rroute(s));
    s >> programs;
    for (int i=0;i<programs;i++) d.programs.push_back(rprogram(s));
    return d;
}

static void wdev(QDataStream &s, const Dev &d) {
    s << d.pos << d.type << d.note;
    if (d.type == 4 || d.type == 5) {
        wchip(s,d.mac,d.ip,d.mask,d.st); s << d.ports; if (d.type == 4) s << d.collision; return;
    }
    s << int(d.ifaces.size());
    for (const auto &f : d.ifaces) { wchip(s,f.mac,f.ip,f.mask,f.st); s << f.name; }
    s << d.routing << int(d.routes.size());
    for (const auto &r : d.routes) wroute(s,r);
    s << int(d.programs.size());
    for (const auto &p : d.programs) wprogram(s,p);
}

static bool has(const QString &s, const QString &needle) { return s.contains(needle, Qt::CaseInsensitive); }
static int findNote(const std::vector<Dev> &devs, const QString &name) {
    for (int i=0;i<int(devs.size());i++) if (has(devs[i].note, name)) return i;
    return -1;
}
static bool pointEq(const QPointF &a, const QPointF &b) { return qAbs(a.x()-b.x()) < 0.01 && qAbs(a.y()-b.y()) < 0.01; }
static bool cableTouches(const Cable &c, const QPointF &p) { return pointEq(c.a,p) || pointEq(c.b,p); }
static void addCable(std::vector<Cable> &cables, const QPointF &a, const QString &pa, const QPointF &b, const QString &pb) {
    cables.push_back({a,b,pa,pb});
}

int main(int argc, char **argv) {
    if (argc != 3) return 2;
    QFile in(argv[1]); if (!in.open(QIODevice::ReadOnly)) return 1;
    QDataStream rs(&in); rs.setVersion(QDataStream::Qt_4_3);
    QString version; int deviceCount; rs >> version >> deviceCount;
    std::vector<Dev> devs; devs.reserve(deviceCount);
    for (int i=0;i<deviceCount;i++) devs.push_back(rdev(rs));
    int cableCount; rs >> cableCount; std::vector<Cable> cables(cableCount);
    for (auto &c : cables) rs >> c.a >> c.b >> c.pa >> c.pb;
    int labelCount; rs >> labelCount; std::vector<Label> labels(labelCount);
    for (auto &l : labels) rs >> l.p >> l.text;
    if (rs.status() != QDataStream::Ok) return 3;

    int sw1 = findNote(devs, "Switch1");
    int sw3 = findNote(devs, "Switch3");
    int pc4 = findNote(devs, "PC4"), pc5 = findNote(devs, "PC5"), pc6 = findNote(devs, "PC6");
    int r1 = findNote(devs, "Router1"), r2 = findNote(devs, "Router2"), r3 = findNote(devs, "Router3");
    if (sw1 < 0 || sw3 < 0 || pc4 < 0 || pc5 < 0 || pc6 < 0 || r2 < 0) return 4;

    QPointF sw3pos = devs[sw3].pos;
    QPointF sw1pos = devs[sw1].pos;
    devs[sw1].ports = 5;

    std::vector<Cable> next;
    for (const auto &c : cables) {
        if (cableTouches(c, sw3pos)) continue;
        if (cableTouches(c, sw1pos)) continue;
        next.push_back(c);
    }

    bool task2 = devs[r1].ifaces.size() == 2 && r3 < 0;
    if (task2) {
        addCable(next, devs[r1].pos, "LAN2", sw1pos, "LAN1");
        addCable(next, devs[r2].pos, "LAN1", sw1pos, "LAN2");
        addCable(next, devs[pc4].pos, "eth0", sw1pos, "LAN3");
        addCable(next, devs[pc5].pos, "eth0", sw1pos, "LAN4");
        addCable(next, devs[pc6].pos, "eth0", sw1pos, "LAN5");
    } else {
        if (r3 < 0) return 5;
        addCable(next, devs[r2].pos, "LAN1", sw1pos, "LAN1");
        addCable(next, devs[r3].pos, "LAN4", sw1pos, "LAN2");
        addCable(next, devs[pc4].pos, "eth0", sw1pos, "LAN3");
        addCable(next, devs[pc5].pos, "eth0", sw1pos, "LAN4");
        addCable(next, devs[pc6].pos, "eth0", sw1pos, "LAN5");
    }
    cables = next;

    std::vector<Dev> outDevs;
    for (int i=0;i<int(devs.size());i++) if (i != sw3) outDevs.push_back(devs[i]);
    devs = outDevs;

    std::vector<Label> outLabels;
    for (auto &l : labels) if (!has(l.text, "Switch3")) outLabels.push_back(l);
    labels = outLabels;

    QFile out(argv[2]); if (!out.open(QIODevice::WriteOnly)) return 6;
    QDataStream ws(&out); ws.setVersion(QDataStream::Qt_4_3);
    ws << version << int(devs.size());
    for (const auto &d : devs) wdev(ws,d);
    ws << int(cables.size()); for (const auto &c : cables) ws << c.a << c.b << c.pa << c.pb;
    ws << int(labels.size()); for (const auto &l : labels) ws << l.p << l.text;
    return ws.status() == QDataStream::Ok ? 0 : 7;
}
