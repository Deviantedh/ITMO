#include <QtCore/QDataStream>
#include <QtCore/QFile>
#include <QtCore/QPointF>
#include <QtCore/QString>

#include <array>
#include <vector>

struct Mac {
    std::array<quint8, 6> b;
};

struct Ip {
    std::array<quint8, 4> b;
};

struct Interface {
    Mac mac;
    Ip ip;
    Ip mask;
    QString name;
};

struct Route {
    Ip dest;
    Ip mask;
    Ip gateway;
    int time;
    qint8 metric;
    Ip out;
};

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

struct Cable {
    QPointF a;
    QPointF b;
    QString portA;
    QString portB;
};

struct Label {
    QPointF pos;
    QString text;
};

static Ip ip(quint8 a, quint8 b, quint8 c, quint8 d)
{
    return Ip{{a, b, c, d}};
}

static Mac mac(quint8 a, quint8 b, quint8 c, quint8 d, quint8 e, quint8 f)
{
    return Mac{{a, b, c, d, e, f}};
}

static void writeMac(QDataStream &s, const Mac &m)
{
    for (quint8 x : m.b) s << x;
}

static void writeIp(QDataStream &s, const Ip &i)
{
    for (quint8 x : i.b) s << x;
}

static QString ipText(const Ip &i)
{
    return QString("%1.%2.%3.%4").arg(i.b[0]).arg(i.b[1]).arg(i.b[2]).arg(i.b[3]);
}

static QString macText(const Mac &m)
{
    return QString("%1:%2:%3:%4:%5:%6")
        .arg(m.b[0], 2, 16, QLatin1Char('0'))
        .arg(m.b[1], 2, 16, QLatin1Char('0'))
        .arg(m.b[2], 2, 16, QLatin1Char('0'))
        .arg(m.b[3], 2, 16, QLatin1Char('0'))
        .arg(m.b[4], 2, 16, QLatin1Char('0'))
        .arg(m.b[5], 2, 16, QLatin1Char('0'))
        .toUpper();
}

static void writeStats(QDataStream &s)
{
    s << quint64(0) << quint64(0) << quint64(0) << quint64(0);
}

static void writeChip(QDataStream &s, const Mac &m, const Ip &addr, const Ip &mask)
{
    writeMac(s, m);
    writeIp(s, addr);
    writeIp(s, mask);
    writeStats(s);
}

static void writeInterface(QDataStream &s, const Interface &iface)
{
    writeChip(s, iface.mac, iface.ip, iface.mask);
    s << iface.name;
}

static void writeRoute(QDataStream &s, const Route &r)
{
    writeIp(s, r.dest);
    writeIp(s, r.mask);
    writeIp(s, r.gateway);
    s << r.time << r.metric;
    writeIp(s, r.out);
}

static void writeDevice(QDataStream &s, const Device &d)
{
    s << d.pos;
    s << d.type;
    s << d.note;

    if (d.type == 4 || d.type == 5) {
        writeChip(s, d.mac, d.ip, d.mask);
        s << d.ports;
        if (d.type == 4) s << d.collision;
        return;
    }

    s << int(d.interfaces.size());
    for (const Interface &iface : d.interfaces) writeInterface(s, iface);
    s << d.routing;
    s << int(d.routes.size());
    for (const Route &r : d.routes) writeRoute(s, r);
    s << int(0);
}

static Device pc(QString name, QPointF p, Mac m, Ip addr, Ip gw)
{
    Device d;
    d.pos = p;
    d.type = 3;
    d.note = QString("<b>%1</b><!--You can use HTML.-->").arg(name);
    d.interfaces.push_back({m, addr, ip(255, 255, 255, 0), "eth0"});
    d.routes.push_back({ip(0, 0, 0, 0), ip(0, 0, 0, 0), gw, 0, 1, addr});
    return d;
}

static Device router(QPointF p)
{
    Device d;
    d.pos = p;
    d.type = 7;
    d.note = "<b>Router1</b><!--You can use HTML.-->";
    d.routing = true;
    d.interfaces = {
        {mac(0x01,0xA0,0x00,0x00,0x01,0x01), ip(135,10,7,1), ip(255,255,255,0), "LAN1"},
        {mac(0x01,0xA0,0x00,0x00,0x01,0x02), ip(135,10,8,1), ip(255,255,255,0), "LAN2"},
        {mac(0x01,0xA0,0x00,0x00,0x01,0x03), ip(135,10,9,1), ip(255,255,255,0), "LAN3"},
    };
    return d;
}

static Device box(int type, QString name, QPointF p, Mac m, int ports)
{
    Device d;
    d.pos = p;
    d.type = type;
    d.note = QString("<b>%1</b><!--You can use HTML.-->").arg(name);
    d.mac = m;
    d.ip = ip(0, 0, 0, 0);
    d.mask = ip(0, 0, 0, 0);
    d.ports = ports;
    return d;
}

static Label label(QPointF p, QString name, Ip addr, Mac m)
{
    return {p, QString("%1\nIPv4: %2\nMAC: %3").arg(name, ipText(addr), macText(m))};
}

int main(int argc, char **argv)
{
    if (argc != 2) return 2;

    const Ip gw1 = ip(135,10,7,1);
    const Ip gw2 = ip(135,10,8,1);
    const Ip gw3 = ip(135,10,9,1);

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
        pc("PC1", {90, 90}, pc1m, ip(135,10,7,13), gw1),
        pc("PC2", {90, 170}, pc2m, ip(135,10,7,14), gw1),
        pc("PC3", {90, 250}, pc3m, ip(135,10,7,15), gw1),
        box(4, "HUB1", {240, 170}, mac(0x01,0x19,0x08,0x84,0x37,0x3E), 4),
        router({430, 250}),
        pc("PC4", {640, 70}, pc4m, ip(135,10,8,16), gw2),
        pc("PC5", {640, 150}, pc5m, ip(135,10,8,17), gw2),
        pc("PC6", {640, 230}, pc6m, ip(135,10,8,18), gw2),
        box(5, "Switch1", {520, 150}, mac(0x01,0x6A,0xD4,0x1F,0x74,0x5C), 4),
        pc("PC7", {640, 330}, pc7m, ip(135,10,9,19), gw3),
        pc("PC8", {640, 410}, pc8m, ip(135,10,9,20), gw3),
        pc("PC9", {640, 490}, pc9m, ip(135,10,9,21), gw3),
        box(5, "Switch2", {520, 410}, mac(0x01,0xBF,0xE0,0x39,0x55,0xFF), 4),
    };

    std::vector<Cable> cables = {
        {{90,90}, {240,170}, "eth0", "LAN1"},
        {{90,170}, {240,170}, "eth0", "LAN2"},
        {{90,250}, {240,170}, "eth0", "LAN3"},
        {{240,170}, {430,250}, "LAN4", "LAN1"},
        {{430,250}, {520,150}, "LAN2", "LAN1"},
        {{640,70}, {520,150}, "eth0", "LAN2"},
        {{640,150}, {520,150}, "eth0", "LAN3"},
        {{640,230}, {520,150}, "eth0", "LAN4"},
        {{430,250}, {520,410}, "LAN3", "LAN1"},
        {{640,330}, {520,410}, "eth0", "LAN2"},
        {{640,410}, {520,410}, "eth0", "LAN3"},
        {{640,490}, {520,410}, "eth0", "LAN4"},
    };

    std::vector<Label> labels = {
        label({20, 50}, "PC1", ip(135,10,7,13), pc1m),
        label({20, 130}, "PC2", ip(135,10,7,14), pc2m),
        label({20, 210}, "PC3", ip(135,10,7,15), pc3m),
        {QPointF(145, 45), "Сеть 1"},
        {QPointF(190, 195), "HUB1\nMAC: 01:19:08:84:37:3E"},
        {QPointF(390, 130), "Router1\nLAN1: 135.10.7.1\nLAN2: 135.10.8.1\nLAN3: 135.10.9.1"},
        {QPointF(470, 80), "Switch1\nMAC: 01:6A:D4:1F:74:5C"},
        {QPointF(570, 20), "Сеть 2"},
        label({680, 30}, "PC4", ip(135,10,8,16), pc4m),
        label({680, 110}, "PC5", ip(135,10,8,17), pc5m),
        label({680, 190}, "PC6", ip(135,10,8,18), pc6m),
        {QPointF(470, 460), "Switch2\nMAC: 01:BF:E0:39:55:FF"},
        {QPointF(570, 290), "Сеть 3"},
        label({680, 290}, "PC7", ip(135,10,9,19), pc7m),
        label({680, 370}, "PC8", ip(135,10,9,20), pc8m),
        label({680, 450}, "PC9", ip(135,10,9,21), pc9m),
    };

    QFile file(argv[1]);
    if (!file.open(QIODevice::WriteOnly)) return 1;

    QDataStream s(&file);
    s.setVersion(QDataStream::Qt_4_3);
    s << QString("0.9.6");
    s << int(devices.size());
    for (const Device &d : devices) writeDevice(s, d);
    s << int(cables.size());
    for (const Cable &c : cables) {
        s << c.a << c.b << c.portA << c.portB;
    }
    s << int(labels.size());
    for (const Label &l : labels) {
        s << l.pos << l.text;
    }

    return s.status() == QDataStream::Ok ? 0 : 3;
}
