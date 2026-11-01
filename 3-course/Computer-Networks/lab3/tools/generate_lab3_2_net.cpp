#include <QtCore/QDataStream>
#include <QtCore/QFile>
#include <QtCore/QPointF>
#include <QtCore/QString>

#include <array>
#include <vector>

struct Mac { std::array<quint8, 6> b; };
struct Ip { std::array<quint8, 4> b; };
struct Interface { Mac mac; Ip ip; Ip mask; QString name; };
struct Route { Ip dest; Ip mask; Ip gateway; int time; qint8 metric; Ip out; };
struct Device {
    QPointF pos;
    int type;
    QString note;
    Mac mac;
    Ip ip;
    Ip mask;
    int ports = 0;
    int collision = 0;
    bool routing = false;
    std::vector<Interface> interfaces;
    std::vector<Route> routes;
};
struct Cable { QPointF a; QPointF b; QString portA; QString portB; };
struct Label { QPointF pos; QString text; };

static Ip ip(quint8 a, quint8 b, quint8 c, quint8 d) { return Ip{{a,b,c,d}}; }
static Mac mac(quint8 a, quint8 b, quint8 c, quint8 d, quint8 e, quint8 f) { return Mac{{a,b,c,d,e,f}}; }

static QString ipText(const Ip &i)
{
    return QString("%1.%2.%3.%4").arg(i.b[0]).arg(i.b[1]).arg(i.b[2]).arg(i.b[3]);
}

static QString macText(const Mac &m)
{
    return QString("%1:%2:%3:%4:%5:%6")
        .arg(m.b[0], 2, 16, QLatin1Char('0')).arg(m.b[1], 2, 16, QLatin1Char('0'))
        .arg(m.b[2], 2, 16, QLatin1Char('0')).arg(m.b[3], 2, 16, QLatin1Char('0'))
        .arg(m.b[4], 2, 16, QLatin1Char('0')).arg(m.b[5], 2, 16, QLatin1Char('0')).toUpper();
}

static void writeMac(QDataStream &s, const Mac &m) { for (quint8 x : m.b) s << x; }
static void writeIp(QDataStream &s, const Ip &i) { for (quint8 x : i.b) s << x; }
static void writeStats(QDataStream &s) { s << quint64(0) << quint64(0) << quint64(0) << quint64(0); }
static void writeChip(QDataStream &s, const Mac &m, const Ip &addr, const Ip &mask)
{
    writeMac(s, m); writeIp(s, addr); writeIp(s, mask); writeStats(s);
}
static void writeInterface(QDataStream &s, const Interface &iface)
{
    writeChip(s, iface.mac, iface.ip, iface.mask);
    s << iface.name;
}
static void writeRoute(QDataStream &s, const Route &r)
{
    writeIp(s, r.dest); writeIp(s, r.mask); writeIp(s, r.gateway);
    s << r.time << r.metric; writeIp(s, r.out);
}
static void writeDevice(QDataStream &s, const Device &d)
{
    s << d.pos << d.type << d.note;
    if (d.type == 4 || d.type == 5) {
        writeChip(s, d.mac, d.ip, d.mask);
        s << d.ports;
        if (d.type == 4) s << d.collision;
        return;
    }
    s << int(d.interfaces.size());
    for (const Interface &iface : d.interfaces) writeInterface(s, iface);
    s << d.routing << int(d.routes.size());
    for (const Route &r : d.routes) writeRoute(s, r);
    s << int(0);
}

static Device pc(QString name, QPointF p, Mac m, Ip addr, Ip gw)
{
    Device d;
    d.pos = p; d.type = 3; d.note = QString("<b>%1</b><!--You can use HTML.-->").arg(name);
    d.interfaces.push_back({m, addr, ip(255,255,255,0), "eth0"});
    d.routes.push_back({ip(0,0,0,0), ip(0,0,0,0), gw, 0, 1, addr});
    return d;
}

static Device box(int type, QString name, QPointF p, Mac m, int ports)
{
    Device d;
    d.pos = p; d.type = type; d.note = QString("<b>%1</b><!--You can use HTML.-->").arg(name);
    d.mac = m; d.ip = ip(0,0,0,0); d.mask = ip(0,0,0,0); d.ports = ports;
    return d;
}

static Device router1(QPointF p)
{
    Device d;
    d.pos = p; d.type = 7; d.note = "<b>Router1</b><!--You can use HTML.-->";
    d.routing = true;
    d.interfaces = {
        {mac(0x01,0xA0,0x00,0x00,0x02,0x01), ip(135,10,7,1), ip(255,255,255,0), "LAN1"},
        {mac(0x01,0xA0,0x00,0x00,0x02,0x02), ip(135,10,8,1), ip(255,255,255,0), "LAN2"},
    };
    d.routes.push_back({ip(135,10,9,0), ip(255,255,255,0), ip(135,10,8,2), 0, 1, ip(135,10,8,1)});
    return d;
}

static Device router2(QPointF p)
{
    Device d;
    d.pos = p; d.type = 7; d.note = "<b>Router2</b><!--You can use HTML.-->";
    d.routing = true;
    d.interfaces = {
        {mac(0x01,0xA0,0x00,0x00,0x03,0x01), ip(135,10,8,2), ip(255,255,255,0), "LAN1"},
        {mac(0x01,0xA0,0x00,0x00,0x03,0x02), ip(135,10,9,1), ip(255,255,255,0), "LAN2"},
    };
    d.routes.push_back({ip(135,10,7,0), ip(255,255,255,0), ip(135,10,8,1), 0, 1, ip(135,10,8,2)});
    return d;
}

static Label label(QPointF p, QString name, Ip addr, Mac m)
{
    return {p, QString("%1\nIPv4: %2\nMAC: %3").arg(name, ipText(addr), macText(m))};
}

int main(int argc, char **argv)
{
    if (argc != 2) return 2;
    const Ip gw1 = ip(135,10,7,1), gw2 = ip(135,10,8,1), gw3 = ip(135,10,9,1);
    const Mac pc1m = mac(0x01,0x75,0xE5,0xF3,0x3C,0x6A);
    const Mac pc2m = mac(0x01,0x38,0x85,0xF3,0x6C,0xA6);
    const Mac pc3m = mac(0x01,0x4D,0xFC,0xBC,0xF7,0x76);
    const Mac pc4m = mac(0x01,0x11,0x67,0xA3,0x0F,0xEC);
    const Mac pc5m = mac(0x01,0x8B,0x82,0x12,0xEA,0x6A);
    const Mac pc6m = mac(0x01,0x4E,0x42,0xDA,0x55,0x4E);
    const Mac pc7m = mac(0x01,0x58,0xA9,0x57,0xEB,0xAB);
    const Mac pc8m = mac(0x01,0x86,0x26,0xEA,0x0E,0x65);
    const Mac pc9m = mac(0x01,0x7F,0x69,0x6D,0x1F,0x41);

    std::vector<Device> devices = {
        pc("PC1", {225,275}, pc1m, ip(135,10,7,13), gw1),
        pc("PC2", {225,375}, pc2m, ip(135,10,7,14), gw1),
        pc("PC3", {225,475}, pc3m, ip(135,10,7,15), gw1),
        box(4, "HUB1", {375,375}, mac(0x01,0x19,0x08,0x84,0x37,0x3E), 4),
        router1({575,325}),
        pc("PC4", {775,125}, pc4m, ip(135,10,8,16), gw2),
        pc("PC5", {775,225}, pc5m, ip(135,10,8,17), gw2),
        pc("PC6", {775,325}, pc6m, ip(135,10,8,18), gw2),
        box(5, "Switch1", {675,225}, mac(0x01,0x6A,0xD4,0x1F,0x74,0x5C), 4),
        box(5, "Switch3", {650,375}, mac(0x01,0xC0,0x12,0x30,0x44,0x5A), 4),
        router2({675,475}),
        pc("PC7", {875,475}, pc7m, ip(135,10,9,19), gw3),
        pc("PC8", {875,575}, pc8m, ip(135,10,9,20), gw3),
        pc("PC9", {875,675}, pc9m, ip(135,10,9,21), gw3),
        box(5, "Switch2", {775,575}, mac(0x01,0xBF,0xE0,0x39,0x55,0xFF), 4),
    };

    std::vector<Cable> cables = {
        {{225,275}, {375,375}, "eth0", "LAN1"},
        {{225,375}, {375,375}, "eth0", "LAN2"},
        {{225,475}, {375,375}, "eth0", "LAN3"},
        {{375,375}, {575,325}, "LAN4", "LAN1"},
        {{575,325}, {650,375}, "LAN2", "LAN1"},
        {{775,125}, {675,225}, "eth0", "LAN2"},
        {{775,225}, {675,225}, "eth0", "LAN3"},
        {{775,325}, {675,225}, "eth0", "LAN4"},
        {{675,225}, {650,375}, "LAN1", "LAN3"},
        {{650,375}, {675,475}, "LAN2", "LAN1"},
        {{675,475}, {775,575}, "LAN2", "LAN1"},
        {{875,475}, {775,575}, "eth0", "LAN2"},
        {{875,575}, {775,575}, "eth0", "LAN3"},
        {{875,675}, {775,575}, "eth0", "LAN4"},
    };

    std::vector<Label> labels = {
        label({57,245}, "PC1", ip(135,10,7,13), pc1m),
        label({59,349}, "PC2", ip(135,10,7,14), pc2m),
        label({53,448}, "PC3", ip(135,10,7,15), pc3m),
        {QPointF(354,272), "Сеть 1"},
        {QPointF(354,304), "HUB1\nMAC: 01:19:08:84:37:3E"},
        {QPointF(488,230), "Router1\nLAN1: 135.10.7.1\nLAN2: 135.10.8.1"},
        {QPointF(542,160), "Switch1\nMAC: 01:6A:D4:1F:74:5C"},
        {QPointF(562,360), "Switch3\nMAC: 01:C0:12:30:44:5A"},
        {QPointF(653,124), "Сеть 2"},
        label({806,97}, "PC4", ip(135,10,8,16), pc4m),
        label({808,194}, "PC5", ip(135,10,8,17), pc5m),
        label({808,297}, "PC6", ip(135,10,8,18), pc6m),
        {QPointF(592,500), "Router2\nLAN1: 135.10.8.2\nLAN2: 135.10.9.1"},
        {QPointF(728,628), "Switch2\nMAC: 01:BF:E0:39:55:FF"},
        {QPointF(760,445), "Сеть 3"},
        label({906,449}, "PC7", ip(135,10,9,19), pc7m),
        label({906,548}, "PC8", ip(135,10,9,20), pc8m),
        label({911,649}, "PC9", ip(135,10,9,21), pc9m),
    };

    QFile file(argv[1]);
    if (!file.open(QIODevice::WriteOnly)) return 1;
    QDataStream s(&file);
    s.setVersion(QDataStream::Qt_4_3);
    s << QString("0.9.6");
    s << int(devices.size());
    for (const Device &d : devices) writeDevice(s, d);
    s << int(cables.size());
    for (const Cable &c : cables) s << c.a << c.b << c.portA << c.portB;
    s << int(labels.size());
    for (const Label &l : labels) s << l.pos << l.text;
    return s.status() == QDataStream::Ok ? 0 : 3;
}
