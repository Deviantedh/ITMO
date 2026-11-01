#include <QtCore/QDataStream>
#include <QtCore/QFile>
#include <QtCore/QPointF>
#include <QtCore/QString>
#include <QtCore/QTextStream>

static QString ipText(const quint8 b[4])
{
    return QString("%1.%2.%3.%4").arg(b[0]).arg(b[1]).arg(b[2]).arg(b[3]);
}

static QString macText(const quint8 b[6])
{
    return QString("%1:%2:%3:%4:%5:%6")
        .arg(b[0], 2, 16, QLatin1Char('0')).arg(b[1], 2, 16, QLatin1Char('0'))
        .arg(b[2], 2, 16, QLatin1Char('0')).arg(b[3], 2, 16, QLatin1Char('0'))
        .arg(b[4], 2, 16, QLatin1Char('0')).arg(b[5], 2, 16, QLatin1Char('0'))
        .toUpper();
}

static void readMac(QDataStream &s, quint8 out[6])
{
    for (int i = 0; i < 6; i++) s >> out[i];
}

static void readIp(QDataStream &s, quint8 out[4])
{
    for (int i = 0; i < 4; i++) s >> out[i];
}

static void readStats(QDataStream &s)
{
    quint64 a, b, c, d;
    s >> a >> b >> c >> d;
}

static void readChip(QDataStream &s, QTextStream &out, const QString &prefix)
{
    quint8 mac[6], ip[4], mask[4];
    readMac(s, mac);
    readIp(s, ip);
    readIp(s, mask);
    readStats(s);
    out << prefix << "mac=" << macText(mac) << " ip=" << ipText(ip) << " mask=" << ipText(mask) << "\n";
}

static QString readIpValue(QDataStream &s)
{
    quint8 v[4];
    readIp(s, v);
    return ipText(v);
}

static void readRoute(QDataStream &s, QTextStream &out)
{
    const QString dest = readIpValue(s);
    const QString mask = readIpValue(s);
    const QString gw = readIpValue(s);
    int time;
    qint8 metric;
    s >> time >> metric;
    const QString outIp = readIpValue(s);
    out << "    route dest=" << dest << " mask=" << mask
        << " gw=" << gw << " metric=" << int(metric) << " out=" << outIp << "\n";
}

static void readProgram(QDataStream &s, QTextStream &out)
{
    int id;
    bool enabled;
    s >> id >> enabled;
    out << "    program id=" << id << " enabled=" << (enabled ? "true" : "false");
    if (id == 0) {
        int split;
        bool trigger;
        s >> split >> trigger;
        out << " RIP split=" << split << " trigger=" << (trigger ? "true" : "false");
    } else if (id == 1) {
        int offerTime, states;
        s >> offerTime >> states;
        out << " DHCPClient offerTime=" << offerTime << " states=" << states;
        for (int i = 0; i < states; i++) {
            int xid, time;
            QString name;
            s >> xid >> time;
            const QString server = readIpValue(s);
            const QString lastIp = readIpValue(s);
            s >> name;
            out << "\n      state xid=" << xid << " time=" << time
                << " server=" << server << " lastIp=" << lastIp << " iface=" << name;
        }
    } else if (id == 2) {
        int staticCount;
        s >> staticCount;
        out << " DHCPServer staticRecords=" << staticCount;
        for (int i = 0; i < staticCount; i++) {
            quint8 mac[6];
            readMac(s, mac);
            const QString yiaddr = readIpValue(s);
            const QString mask = readIpValue(s);
            const QString gw = readIpValue(s);
            int time;
            s >> time;
            out << "\n      static mac=" << macText(mac) << " ip=" << yiaddr << " mask=" << mask << " gw=" << gw << " time=" << time;
        }
        QString iface;
        s >> iface;
        const QString begin = readIpValue(s);
        const QString end = readIpValue(s);
        const QString mask = readIpValue(s);
        const QString gw = readIpValue(s);
        int lease, waiting;
        bool dynamic;
        s >> lease >> dynamic >> waiting;
        out << "\n      iface=" << iface << " pool=" << begin << "-" << end
            << " mask=" << mask << " gw=" << gw << " lease=" << lease
            << " dynamic=" << (dynamic ? "true" : "false") << " waiting=" << waiting;
    } else {
        out << " unknown";
    }
    out << "\n";
}

int main(int argc, char **argv)
{
    QTextStream out(stdout);
    for (int arg = 1; arg < argc; arg++) {
        QFile file(argv[arg]);
        out << "### " << argv[arg] << "\n";
        if (!file.open(QIODevice::ReadOnly)) {
            out << "cannot open\n";
            continue;
        }
        QDataStream s(&file);
        s.setVersion(QDataStream::Qt_4_3);
        QString version;
        int deviceCount;
        s >> version >> deviceCount;
        out << "version=" << version << " devices=" << deviceCount << "\n";
        for (int i = 0; i < deviceCount; i++) {
            QPointF pos;
            int type;
            QString note;
            s >> pos >> type >> note;
            out << "  device#" << i << " type=" << type << " pos=(" << pos.x() << "," << pos.y() << ") note=" << note << "\n";
            if (type == 4 || type == 5) {
                readChip(s, out, "    ");
                int ports;
                s >> ports;
                out << "    ports=" << ports << "\n";
                if (type == 4) {
                    int collision;
                    s >> collision;
                    out << "    collision=" << collision << "\n";
                }
            } else {
                int ifaces;
                s >> ifaces;
                out << "    interfaces=" << ifaces << "\n";
                for (int j = 0; j < ifaces; j++) {
                    readChip(s, out, "      ");
                    QString name;
                    s >> name;
                    out << "      name=" << name << "\n";
                }
                bool routing;
                int routes, programs;
                s >> routing >> routes;
                out << "    routing=" << (routing ? "true" : "false") << " routes=" << routes << "\n";
                for (int j = 0; j < routes; j++) readRoute(s, out);
                s >> programs;
                out << "    programs=" << programs << "\n";
                for (int j = 0; j < programs; j++) readProgram(s, out);
            }
        }
        int cables;
        s >> cables;
        out << "  cables=" << cables << "\n";
        for (int i = 0; i < cables; i++) {
            QPointF a, b;
            QString pa, pb;
            s >> a >> b >> pa >> pb;
            out << "    cable " << i << " (" << a.x() << "," << a.y() << "):" << pa
                << " -> (" << b.x() << "," << b.y() << "):" << pb << "\n";
        }
        int labels;
        s >> labels;
        out << "  labels=" << labels << "\n";
        for (int i = 0; i < labels; i++) {
            QPointF p;
            QString text;
            s >> p >> text;
            out << "    label " << i << " (" << p.x() << "," << p.y() << ") "
                << text.replace('\n', " / ") << "\n";
        }
        out << "status=" << s.status() << "\n";
    }
    return 0;
}
