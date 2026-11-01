import Modules.DBProvider;
import Modules.Server;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {

//        Server server = new Server(new InetSocketAddress(7779));

//       Строчки для теста на локалхосте. Для гелиуса достаточно указать только порт
//        DBProvider.establishConnection("jdbc:postgresql://localhost:9999/studs", "s413041", "");
        Server server = new Server(new InetSocketAddress("localhost", 8000));
        server.run();
    }
}
