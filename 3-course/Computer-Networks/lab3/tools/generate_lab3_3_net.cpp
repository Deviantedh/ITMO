#include <QtCore/QDataStream>
#include <QtCore/QFile>
#include <QtCore/QPointF>
#include <QtCore/QString>

#include <array>
#include <vector>

struct Mac { std::array<quint8, 6> b; };
struct Ip { std::array<quint8, 4> b; };
struct Iface { Mac mac; Ip ip; Ip mask; QString name; };
struct Route { Ip dest; Ip mask; Ip gw; int time; qint8 metric; Ip out; };
struct Dev {
    QPointF pos; int type; QString note; Mac mac; Ip ip; Ip mask;
    int ports = 0; int collision = 0; bool routing = false;
    std::vector<Iface> ifaces; std::vector<Route> routes;
};
struct Cable { QPointF a; QPointF b; QString pa; QString pb; };
struct Label { QPointF p; QString text; };

static Ip ip(quint8 a, quint8 b, quint8 c, quint8 d) { return Ip{{a,b,c,d}}; }
static Mac mac(quint8 a, quint8 b, quint8 c, quint8 d, quint8 e, quint8 f) { return Mac{{a,b,c,d,e,f}}; }
static QString ipText(const Ip &x) { return QString("%1.%2.%3.%4").arg(x.b[0]).arg(x.b[1]).arg(x.b[2]).arg(x.b[3]); }
static QString macText(const Mac &m) {
    return QString("%1:%2:%3:%4:%5:%6")
        .arg(m.b[0],2,16,QLatin1Char('0')).arg(m.b[1],2,16,QLatin1Char('0'))
        .arg(m.b[2],2,16,QLatin1Char('0')).arg(m.b[3],2,16,QLatin1Char('0'))
        .arg(m.b[4],2,16,QLatin1Char('0')).arg(m.b[5],2,16,QLatin1Char('0')).toUpper();
}
static void wmac(QDataStream &s, const Mac &m) { for (quint8 x : m.b) s << x; }
static void wip(QDataStream &s, const Ip &x) { for (quint8 b : x.b) s << b; }
static void wstats(QDataStream &s) { s << quint64(0) << quint64(0) << quint64(0) << quint64(0); }
static void wchip(QDataStream &s, const Mac &m, const Ip &addr, const Ip &mask) { wmac(s,m); wip(s,addr); wip(s,mask); wstats(s); }
static void wroute(QDataStream &s, const Route &r) { wip(s,r.dest); wip(s,r.mask); wip(s,r.gw); s << r.time << r.metric; wip(s,r.out); }
static void wdev(QDataStream &s, const Dev &d) {
    s << d.pos << d.type << d.note;
    if (d.type == 4 || d.type == 5) {
        wchip(s, d.mac, d.ip, d.mask); s << d.ports; if (d.type == 4) s << d.collision; return;
    }
    s << int(d.ifaces.size());
    for (const auto &i : d.ifaces) { wchip(s, i.mac, i.ip, i.mask); s << i.name; }
    s << d.routing << int(d.routes.size());
    for (const auto &r : d.routes) wroute(s, r);
    s << int(0);
}

static Dev pc(QString name, QPointF p, Mac m, Ip addr, Ip gw) {
    Dev d; d.pos=p; d.type=3; d.note=QString("<b>%1</b><!--You can use HTML.-->").arg(name);
    d.ifaces.push_back({m, addr, ip(255,255,255,0), "eth0"});
    d.routes.push_back({ip(0,0,0,0), ip(0,0,0,0), gw, 0, 0, addr});
    return d;
}
static Dev box(int type, QString name, QPointF p, Mac m, int ports) {
    Dev d; d.pos=p; d.type=type; d.note=QString("<b>%1</b><!--You can use HTML.-->").arg(name);
    d.mac=m; d.ip=ip(0,0,0,0); d.mask=ip(0,0,0,0); d.ports=ports; return d;
}
static Label label(QPointF p, QString name, Ip addr, Mac m) {
    return {p, QString("%1\nIPv4: %2\nMAC: %3").arg(name, ipText(addr), macText(m))};
}

static Dev r1() {
    Dev d; d.pos={520,300}; d.type=7; d.note="<b>Router1</b><!--You can use HTML.-->"; d.routing=true;
    d.ifaces = {
        {mac(1,0xA0,0,0,4,1), ip(135,10,7,1), ip(255,255,255,0), "LAN1"},
        {mac(1,0xA0,0,0,4,2), ip(135,10,10,1), ip(255,255,255,0), "LAN2"},
        {mac(1,0xA0,0,0,4,3), ip(135,10,12,1), ip(255,255,255,0), "LAN3"},
    };
    d.routes = {
        {ip(135,10,8,0), ip(255,255,255,0), ip(135,10,12,2), 0, 0, ip(135,10,12,1)},
        {ip(135,10,9,0), ip(255,255,255,0), ip(135,10,12,2), 0, 0, ip(135,10,12,1)},
    };
    return d;
}
static Dev r2() {
    Dev d; d.pos={680,380}; d.type=7; d.note="<b>Router2</b><!--You can use HTML.-->"; d.routing=true;
    d.ifaces = {
        {mac(1,0xA0,0,0,5,1), ip(135,10,8,1), ip(255,255,255,0), "LAN1"},
        {mac(1,0xA0,0,0,5,2), ip(135,10,10,2), ip(255,255,255,0), "LAN2"},
        {mac(1,0xA0,0,0,5,3), ip(135,10,11,1), ip(255,255,255,0), "LAN3"},
    };
    d.routes = {
        {ip(135,10,7,0), ip(255,255,255,0), ip(135,10,10,1), 0, 0, ip(135,10,10,2)},
        {ip(135,10,9,0), ip(255,255,255,0), ip(135,10,11,2), 0, 0, ip(135,10,11,1)},
    };
    return d;
}
static Dev r3() {
    Dev d; d.pos={680,560}; d.type=7; d.note="<b>Router3</b><!--You can use HTML.-->"; d.routing=true;
    d.ifaces = {
        {mac(1,0xA0,0,0,6,1), ip(135,10,9,1), ip(255,255,255,0), "LAN1"},
        {mac(1,0xA0,0,0,6,2), ip(135,10,12,2), ip(255,255,255,0), "LAN2"},
        {mac(1,0xA0,0,0,6,3), ip(135,10,11,2), ip(255,255,255,0), "LAN3"},
        {mac(1,0xA0,0,0,6,4), ip(135,10,8,2), ip(255,255,255,0), "LAN4"},
    };
    d.routes = {
        {ip(135,10,7,0), ip(255,255,255,0), ip(135,10,12,1), 0, 0, ip(135,10,12,2)},
    };
    return d;
}

int main(int argc, char **argv) {
    if (argc != 2) return 2;
    const Mac pc1m=mac(1,0x75,0xE5,0xF3,0x3C,0x6A), pc2m=mac(1,0x38,0x85,0xF3,0x6C,0xA6), pc3m=mac(1,0x4D,0xFC,0xBC,0xF7,0x76);
    const Mac pc4m=mac(1,0x11,0x67,0xA3,0x0F,0xEC), pc5m=mac(1,0x8B,0x82,0x12,0xEA,0x6A);
    const Mac pc7m=mac(1,0x58,0xA9,0x57,0xEB,0xAB), pc8m=mac(1,0x86,0x26,0xEA,0x0E,0x65), pc9m=mac(1,0x7F,0x69,0x6D,0x1F,0x41);
    std::vector<Dev> devs = {
        pc("PC1",{180,220},pc1m,ip(135,10,7,13),ip(135,10,7,1)),
        pc("PC2",{180,320},pc2m,ip(135,10,7,14),ip(135,10,7,1)),
        box(4,"HUB1",{340,320},mac(1,0x19,0x08,0x84,0x37,0x3E),4),
        r1(), r2(), r3(),
        box(5,"Switch1",{840,250},mac(1,0x6A,0xD4,0x1F,0x74,0x5C),4),
        pc("PC4",{980,150},pc4m,ip(135,10,8,16),ip(135,10,8,2)),
        pc("PC5",{980,250},pc5m,ip(135,10,8,17),ip(135,10,8,2)),
        box(5,"Switch2",{840,640},mac(1,0xBF,0xE0,0x39,0x55,0xFF),4),
        pc("PC7",{980,560},pc7m,ip(135,10,9,19),ip(135,10,9,1)),
        pc("PC8",{980,660},pc8m,ip(135,10,9,20),ip(135,10,9,1)),
        pc("PC9",{980,760},pc9m,ip(135,10,9,21),ip(135,10,9,1)),
    };
    std::vector<Cable> cables = {
        {{180,220},{340,320},"eth0","LAN1"}, {{180,320},{340,320},"eth0","LAN2"},
        {{340,320},{520,300},"LAN3","LAN1"},
        {{520,300},{680,380},"LAN2","LAN2"}, {{520,300},{680,560},"LAN3","LAN2"}, {{680,380},{680,560},"LAN3","LAN3"},
        {{680,380},{840,250},"LAN1","LAN1"}, {{680,560},{840,250},"LAN4","LAN2"},
        {{980,150},{840,250},"eth0","LAN3"}, {{980,250},{840,250},"eth0","LAN4"},
        {{680,560},{840,640},"LAN1","LAN1"}, {{980,560},{840,640},"eth0","LAN2"}, {{980,660},{840,640},"eth0","LAN3"}, {{980,760},{840,640},"eth0","LAN4"},
    };
    std::vector<Label> labels = {
        label({40,190},"PC1",ip(135,10,7,13),pc1m), label({40,290},"PC2",ip(135,10,7,14),pc2m),
        {QPointF(310,235),"Сеть 1"}, {QPointF(300,350),"HUB1\nMAC: 01:19:08:84:37:3E"},
        {QPointF(450,205),"Router1\nLAN1: 135.10.7.1\nLAN2: 135.10.10.1\nLAN3: 135.10.12.1"},
        {QPointF(610,290),"Router2\nLAN1: 135.10.8.1\nLAN2: 135.10.10.2\nLAN3: 135.10.11.1"},
        {QPointF(610,590),"Router3\nLAN1: 135.10.9.1\nLAN2: 135.10.12.2\nLAN3: 135.10.11.2\nLAN4: 135.10.8.2"},
        {QPointF(790,125),"Сеть 2"}, {QPointF(780,190),"Switch1\nMAC: 01:6A:D4:1F:74:5C"},
        label({1010,120},"PC4",ip(135,10,8,16),pc4m), label({1010,220},"PC5",ip(135,10,8,17),pc5m),
        {QPointF(780,705),"Switch2\nMAC: 01:BF:E0:39:55:FF"}, {QPointF(790,530),"Сеть 3"},
        label({1010,530},"PC7",ip(135,10,9,19),pc7m), label({1010,630},"PC8",ip(135,10,9,20),pc8m), label({1010,730},"PC9",ip(135,10,9,21),pc9m),
    };
    QFile f(argv[1]); if (!f.open(QIODevice::WriteOnly)) return 1;
    QDataStream s(&f); s.setVersion(QDataStream::Qt_4_3);
    s << QString("0.9.6") << int(devs.size()); for (const auto &d : devs) wdev(s,d);
    s << int(cables.size()); for (const auto &c : cables) s << c.a << c.b << c.pa << c.pb;
    s << int(labels.size()); for (const auto &l : labels) s << l.p << l.text;
    return s.status() == QDataStream::Ok ? 0 : 3;
}
