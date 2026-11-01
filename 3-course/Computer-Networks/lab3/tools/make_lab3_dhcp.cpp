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
    int id = -1;
    bool enabled = true;
    int offerTime = 60;
    QString iface;
    Ip begin{}, end{}, mask{}, gw{};
    int lease = 1200;
    bool dynamic = true;
    int waiting = 60;
};
struct Dev {
    QPointF pos; int type; QString note; Mac mac{}; Ip ip{}; Ip mask{};
    quint64 st[4]{}; int ports = 0; int collision = 0; bool routing = false;
    std::vector<Iface> ifaces; std::vector<Route> routes; std::vector<Program> programs;
};
struct Cable { QPointF a; QPointF b; QString pa; QString pb; };
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
        int split; bool trigger; s >> split >> trigger;
    } else if (p.id == 1) {
        int states; s >> p.offerTime >> states;
        for (int i=0;i<states;i++) {
            int xid, time; Ip server, lastIp; QString name;
            s >> xid >> time; rip(s, server); rip(s, lastIp); s >> name;
        }
    } else if (p.id == 2) {
        int staticCount; s >> staticCount;
        for (int i=0;i<staticCount;i++) {
            Mac m; Ip yiaddr, mask, gw; int time;
            rmac(s,m); rip(s,yiaddr); rip(s,mask); rip(s,gw); s >> time;
        }
        s >> p.iface; rip(s,p.begin); rip(s,p.end); rip(s,p.mask); rip(s,p.gw);
        s >> p.lease >> p.dynamic >> p.waiting;
    }
    return p;
}

static void wprogram(QDataStream &s, const Program &p) {
    s << p.id << p.enabled;
    if (p.id == 0) {
        s << int(0) << bool(true);
    } else if (p.id == 1) {
        s << p.offerTime << int(0);
    } else if (p.id == 2) {
        s << int(0);
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
    for (int i=0;i<ifaces;i++) {
        Iface f; rchip(s,f.mac,f.ip,f.mask,f.st); s >> f.name; d.ifaces.push_back(f);
    }
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

static Program dhcpClient() {
    Program p; p.id = 1; p.enabled = true; p.offerTime = 60; return p;
}

static Program dhcpServer(const QString &iface, Ip begin, Ip end, Ip mask, Ip gw) {
    Program p; p.id = 2; p.enabled = true; p.iface = iface; p.begin = begin; p.end = end;
    p.mask = mask; p.gw = gw; p.lease = 1200; p.dynamic = true; p.waiting = 60; return p;
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

    for (auto &d : devs) {
        d.programs.clear();
        if (d.type == 3 && !d.ifaces.empty()) {
            d.ifaces[0].ip = ip(0,0,0,0);
            d.ifaces[0].mask = ip(0,0,0,0);
            d.routes.clear();
            d.programs.push_back(dhcpClient());
        } else if (d.type == 7) {
            for (const auto &f : d.ifaces) {
                if (eq(f.ip, ip(135,10,7,1))) d.programs.push_back(dhcpServer(f.name, ip(135,10,7,13), ip(135,10,7,30), ip(255,255,255,0), ip(135,10,7,1)));
                if (eq(f.ip, ip(135,10,8,1))) d.programs.push_back(dhcpServer(f.name, ip(135,10,8,16), ip(135,10,8,30), ip(255,255,255,0), ip(135,10,8,1)));
                if (eq(f.ip, ip(135,10,9,1))) d.programs.push_back(dhcpServer(f.name, ip(135,10,9,19), ip(135,10,9,30), ip(255,255,255,0), ip(135,10,9,1)));
            }
        }
    }

    for (auto &l : labels) {
        l.text.replace("IPv4: 135.10.7.13", "IPv4: 135.10.7.[13-30]");
        l.text.replace("IPv4: 135.10.7.14", "IPv4: 135.10.7.[13-30]");
        l.text.replace("IPv4: 135.10.7.15", "IPv4: 135.10.7.[13-30]");
        l.text.replace("IPv4: 135.10.8.16", "IPv4: 135.10.8.[16-30]");
        l.text.replace("IPv4: 135.10.8.17", "IPv4: 135.10.8.[16-30]");
        l.text.replace("IPv4: 135.10.8.18", "IPv4: 135.10.8.[16-30]");
        l.text.replace("IPv4: 135.10.9.19", "IPv4: 135.10.9.[19-30]");
        l.text.replace("IPv4: 135.10.9.20", "IPv4: 135.10.9.[19-30]");
        l.text.replace("IPv4: 135.10.9.21", "IPv4: 135.10.9.[19-30]");
    }

    QFile out(argv[2]); if (!out.open(QIODevice::WriteOnly)) return 4;
    QDataStream ws(&out); ws.setVersion(QDataStream::Qt_4_3);
    ws << version << int(devs.size());
    for (const auto &d : devs) wdev(ws,d);
    ws << int(cables.size()); for (const auto &c : cables) ws << c.a << c.b << c.pa << c.pb;
    ws << int(labels.size()); for (const auto &l : labels) ws << l.p << l.text;
    return ws.status() == QDataStream::Ok ? 0 : 5;
}
