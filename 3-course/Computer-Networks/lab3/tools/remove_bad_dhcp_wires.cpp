#include <QtCore/QDataStream>
#include <QtCore/QFile>
#include <QtCore/QPointF>
#include <QtCore/QString>

#include <vector>

static bool pointEq(const QPointF &a, const QPointF &b)
{
    return qAbs(a.x() - b.x()) < 0.01 && qAbs(a.y() - b.y()) < 0.01;
}

static bool sameCable(const QPointF &a, const QString &pa, const QPointF &b, const QString &pb,
                      const QPointF &x, const QString &px, const QPointF &y, const QString &py)
{
    return (pointEq(a, x) && pa == px && pointEq(b, y) && pb == py)
        || (pointEq(a, y) && pa == py && pointEq(b, x) && pb == px);
}

struct Cable { QPointF a, b; QString pa, pb; };
struct Label { QPointF p; QString text; };

int main(int argc, char **argv)
{
    if (argc != 3) return 2;
    QFile in(argv[1]);
    if (!in.open(QIODevice::ReadOnly)) return 1;
    QByteArray deviceBytes;
    QDataStream rs(&in);
    rs.setVersion(QDataStream::Qt_4_3);

    QString version;
    int deviceCount;
    rs >> version >> deviceCount;
    QBufferLike:
    ;

    QByteArray devicesPayload;
    {
        QDataStream ws(&devicesPayload, QIODevice::WriteOnly);
        ws.setVersion(QDataStream::Qt_4_3);
        for (int i = 0; i < deviceCount; ++i) {
            QPointF pos; int type; QString note;
            rs >> pos >> type >> note;
            ws << pos << type << note;
            if (type == 4 || type == 5) {
                quint8 b; for (int j=0;j<6;j++) { rs >> b; ws << b; }
                for (int j=0;j<4;j++) { rs >> b; ws << b; }
                for (int j=0;j<4;j++) { rs >> b; ws << b; }
                quint64 u; for (int j=0;j<4;j++) { rs >> u; ws << u; }
                int ports; rs >> ports; ws << ports;
                if (type == 4) { int collision; rs >> collision; ws << collision; }
            } else {
                int ifaces; rs >> ifaces; ws << ifaces;
                for (int k=0;k<ifaces;k++) {
                    quint8 b; for (int j=0;j<6;j++) { rs >> b; ws << b; }
                    for (int j=0;j<4;j++) { rs >> b; ws << b; }
                    for (int j=0;j<4;j++) { rs >> b; ws << b; }
                    quint64 u; for (int j=0;j<4;j++) { rs >> u; ws << u; }
                    QString name; rs >> name; ws << name;
                }
                bool routing; int routes; rs >> routing >> routes; ws << routing << routes;
                for (int k=0;k<routes;k++) {
                    quint8 b; for (int j=0;j<12;j++) { rs >> b; ws << b; }
                    int time; qint8 metric; rs >> time >> metric; ws << time << metric;
                    for (int j=0;j<4;j++) { rs >> b; ws << b; }
                }
                int programs; rs >> programs; ws << programs;
                for (int k=0;k<programs;k++) {
                    int id; bool enabled; rs >> id >> enabled; ws << id << enabled;
                    if (id == 0) {
                        int split; bool trigger; rs >> split >> trigger; ws << split << trigger;
                    } else if (id == 1) {
                        int offer, states; rs >> offer >> states; ws << offer << states;
                        for (int s=0;s<states;s++) {
                            int xid, time; QString iface; quint8 b;
                            rs >> xid >> time; ws << xid << time;
                            for (int j=0;j<8;j++) { rs >> b; ws << b; }
                            rs >> iface; ws << iface;
                        }
                    } else if (id == 2) {
                        int statics; rs >> statics; ws << statics;
                        for (int s=0;s<statics;s++) {
                            quint8 b; for (int j=0;j<18;j++) { rs >> b; ws << b; }
                            int time; rs >> time; ws << time;
                        }
                        QString iface; rs >> iface; ws << iface;
                        quint8 b; for (int j=0;j<16;j++) { rs >> b; ws << b; }
                        int lease, waiting; bool dynamic;
                        rs >> lease >> dynamic >> waiting; ws << lease << dynamic << waiting;
                    }
                }
            }
        }
    }

    int cableCount; rs >> cableCount;
    std::vector<Cable> cables;
    for (int i=0;i<cableCount;i++) {
        Cable c; rs >> c.a >> c.b >> c.pa >> c.pb;
        const bool badHubToRouter2 = sameCable(c.a, c.pa, c.b, c.pb, QPointF(325,325), "LAN5", QPointF(675,325), "LAN4");
        const bool badRouter1ToSwitch2 = sameCable(c.a, c.pa, c.b, c.pb, QPointF(575,425), "LAN4", QPointF(675,625), "LAN5");
        if (!badHubToRouter2 && !badRouter1ToSwitch2) cables.push_back(c);
    }

    int labelCount; rs >> labelCount;
    std::vector<Label> labels(labelCount);
    for (auto &l : labels) rs >> l.p >> l.text;
    if (rs.status() != QDataStream::Ok) return 3;

    QFile out(argv[2]);
    if (!out.open(QIODevice::WriteOnly)) return 4;
    QDataStream ws(&out);
    ws.setVersion(QDataStream::Qt_4_3);
    ws << version << deviceCount;
    ws.writeRawData(devicesPayload.constData(), devicesPayload.size());
    ws << int(cables.size());
    for (const auto &c : cables) ws << c.a << c.b << c.pa << c.pb;
    ws << int(labels.size());
    for (const auto &l : labels) ws << l.p << l.text;
    return ws.status() == QDataStream::Ok ? 0 : 5;
}
