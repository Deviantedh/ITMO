import com.fastcgi.FCGIInterface;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    private static List<Map<String, String>> sessionResults = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        FCGIInterface fcgiInterface = new FCGIInterface();
        while (fcgiInterface.FCGIaccept() >= 0) {
//            String method = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
//            if (method == null || !method.equals("POST")) {
//                System.out.println(errorResult("Only POST method accepted"));
//                continue;
//            }
//
            String contentType = FCGIInterface.request.params.getProperty("CONTENT_TYPE");
            if (contentType == null || !contentType.equals("application/x-www-form-urlencoded")) {
                System.out.println(errorResult("Unsupported Content-Type"));
                continue;
            }

            Map<String, String> requestBody = parseFormUrlEncoded(readRequestBody());
//            String xValStr = args[0];
//            String yValStr = args[1];
//            String rValStr = args[2];
            String xValStr = requestBody.get("xVal");
            String yValStr = requestBody.get("yVal");
            String rValStr = requestBody.get("rVal");

            if (xValStr == null || yValStr == null || rValStr == null) {
                System.out.println(errorResult("Missing parameters"));
                continue;
            }

            try {
                double x = Double.parseDouble(xValStr);
                double y = Double.parseDouble(yValStr);
                double r = Double.parseDouble(rValStr);

                CoordinatesValidator validator = new CoordinatesValidator(x, y, r);
                if (validator.checkData()) {
                    boolean isInArea = AreaChecker.isInArea(x, y, r);
                    String coordsStatus = isInArea ? "Hit" : "Didn't hit";

                    String currentTime = new Date().toString();
                    double benchmarkTime = System.nanoTime();

//                    Map<String, String> newResult = new HashMap<>();
//                    newResult.put("x", String.valueOf(x));
//                    newResult.put("y", String.valueOf(y));
//                    newResult.put("r", String.valueOf(r));
//                    newResult.put("coordsStatus", coordsStatus);
//                    newResult.put("currentTime", currentTime);
//                    newResult.put("benchmarkTime", String.valueOf(benchmarkTime));

                    String jsonResponse = "{"
                            + "\"x\":\"" + x + "\","
                            + "\"y\":\"" + y + "\","
                            + "\"r\":\"" + r + "\","
                            + "\"coordsStatus\":\"" + coordsStatus + "\","
                            + "\"currentTime\":\"" + currentTime + "\","
                            + "\"benchmarkTime\":\"" + benchmarkTime + "\""
                            + "}";

                    System.out.println("Content-type: application/json\r\n");
                    System.out.println(jsonResponse);
                } else {
                    System.out.println(errorResult("Invalid data"));
                }

            } catch (NumberFormatException e) {
                System.out.println(errorResult("Invalid number format"));
            }
        }
    }

//    private static Map<String, String> parseFormUrlEncoded(String requestBody) {
//        Map<String, String> params = new HashMap<>();
//        for (String pair : requestBody.split("&")) {
//            String[] keyValue = pair.split("=");
//            if (keyValue.length == 2) {
//                params.put(keyValue[0], keyValue[1]);
//            }
//        }
//        return params;
//    }

//    private static String readRequestBody() throws IOException {
//        FCGIInterface.request.inStream.fill();
//        int contentLength = FCGIInterface.request.inStream.available();
//        ByteBuffer buffer = ByteBuffer.allocate(contentLength);
//        int readBytes = FCGIInterface.request.inStream.read(buffer.array(), 0, contentLength);
//
//        byte[] requestBodyRaw = new byte[readBytes];
//        buffer.get(requestBodyRaw);
//        buffer.clear();
//
//        return new String(requestBodyRaw, StandardCharsets.UTF_8);
//    }

    private static String errorResult(String error) {
        return String.format("HTTP/1.1 400 Bad Request\nContent-Type: text/html\n\n%s", error);
    }

//    private static String renderTable(List<Map<String, String>> results) {
//        StringBuilder table = new StringBuilder("<table><thead><tr><th>x</th><th>y</th><th>r</th><th>Status</th><th>Time</th><th>Execution Time</th></tr></thead><tbody>");
//        for (Map<String, String> result : results) {
//            table.append("<tr>");
//            table.append("<td>").append(result.get("x")).append("</td>");
//            table.append("<td>").append(result.get("y")).append("</td>");
//            table.append("<td>").append(result.get("r")).append("</td>");
//            table.append("<td>").append(result.get("coordsStatus")).append("</td>");
//            table.append("<td>").append(result.get("currentTime")).append("</td>");
//            table.append("<td>").append(result.get("benchmarkTime")).append("</td>");
//            table.append("</tr>");
//        }
//        table.append("</tbody></table>");
//        return String.format("HTTP/1.1 200 OK\nContent-Type: text/html\n\n%s", table.toString());
//    }
}
