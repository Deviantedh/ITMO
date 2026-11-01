import com.fastcgi.FCGIInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;


public class Main{
    private static List<Map<String, String>> sessionResults = new ArrayList<>();
    private static HashMap<String, Object> singleBody;       // Для одиночного объекта
    private static List<HashMap<String, Object>> bodyList;   // Для списка объектов


    public static void main(String[] args) throws IOException {
        FCGIInterface fcgiInterface = new FCGIInterface();
        singleBody = null;
        bodyList = null;
        while (fcgiInterface.FCGIaccept() >= 0) {
            try {
            String method = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
            if (method == null || !method.equals("POST")) {
                sendError("Only POST method accepted");
                continue;
            }

            var startTime = Instant.now();

            String URI = FCGIInterface.request.params.getProperty("REQUEST_URI");
            int pathEndIndex = URI.indexOf("/fcgi-bin/web1.jar");
            String pathAfterWeb1Jar = URI.substring(pathEndIndex + "/fcgi-bin/web1.jar".length());
            if (pathAfterWeb1Jar.contains("/")) {
                sendError("Запрос с дополнительными путями запрещен");
                continue;
            }

            String contentLength = FCGIInterface.request.params.getProperty("CONTENT_LENGTH");

            int length = (contentLength != null) ? Integer.parseInt(contentLength) : 0;

            if (length > 0) {
                try {
                    sendError("yt");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                    char[] bodyChars = new char[length];
                    reader.read(bodyChars, 0, length);
                    String requestBody = new String(bodyChars);
                    sendError(requestBody);
                    parseBody(requestBody);


                } catch (Exception e) {
                    singleBody = new HashMap<>();
                    singleBody.put("error", "Ошибка при чтении тела запроса");
                }
            }

            /*
            BufferedReader reader = new BufferedReader(new InputStreamReader(FCGIInterface.request.inStream));

            StringBuilder bodyBuilder = new StringBuilder();
            String line;


            while ((line = reader.readLine()) != null) {
                bodyBuilder.append(line);
            }

// Преобразуем строку body в JSON-объект
            String requestBody = bodyBuilder.toString();
            JSONObject jsonObject = new JSONObject(requestBody);
// Получаем значения параметров
            String xValStr = jsonObject.getString("xVal");
            String yValStr = jsonObject.getString("yVal");
            String rValStr = jsonObject.getString("rVal");

             */

            // Получаем параметры из QUERY_STRING
            String queryString = FCGIInterface.request.params.getProperty("QUERY_STRING");
            if (queryString == null || queryString.isEmpty()) {
                sendError("Missing parameters in URL");
                continue;
            }

            Map<String, String> params = parseQueryString(queryString);


            try {
//                processSingleObject(singleBody);
                String xValStr = params.get("xVal");
                String yValStr = params.get("yVal"); //проверка на отсутствие
                String rValStr = params.get("rVal");
                double x = Double.parseDouble(xValStr);
                double y = Double.parseDouble(yValStr);
                double r = Double.parseDouble(rValStr);
//                double x = Double.parseDouble(String.valueOf(singleBody.get("xVal")));
//                double y = Double.parseDouble(String.valueOf(singleBody.get("yVal")));
//                double r = Double.parseDouble(String.valueOf(singleBody.get("rVal")));

                CoordinatesValidator validator = new CoordinatesValidator(x, y, r);
                if (validator.checkData()) {
                    boolean isInArea = AreaChecker.isInArea(x, y, r);
                    String coordsStatus = isInArea ? "Hit" : "Didn't hit";

                    String currentTime = new Date().toString();
                    var endTime = Instant.now();
                    long timeTakenNanos = ChronoUnit.NANOS.between(startTime, endTime);
                    double benchmarkTime = Double.parseDouble(String.valueOf(timeTakenNanos));

                    Map<String, String> newResult = new HashMap<>();
                    newResult.put("x", String.valueOf(x));
                    newResult.put("y", String.valueOf(y));
                    newResult.put("r", String.valueOf(r));
                    newResult.put("coordsStatus", coordsStatus);
                    newResult.put("currentTime", currentTime);
                    newResult.put("benchmarkTime", String.valueOf(benchmarkTime));

                    sessionResults.add(newResult);

                    String jsonResponse = renderJson(sessionResults);
                    sendJsonResponse(jsonResponse);
                } else {
                    sendError("Invalid data");
                }

            } catch (NumberFormatException e) {
                sendError("Invalid number format");
            }
        } catch (NumberFormatException e) {
                sendError("ничего не работает");
            }
        }
    }

    private static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

    private static void sendError(String error) {
        System.out.print("Status: 400 Bad Request\n");
        System.out.println("Content-Type: text/plain\n\n");
        System.out.println(error);
    }
    private static void parseBody(String requestBody) {
        if (requestBody != null && !requestBody.isEmpty()) {
            try {
                com.google.gson.JsonElement jsonElement = com.google.gson.JsonParser.parseString(requestBody);
                if (jsonElement.isJsonArray()) {
                    com.google.gson.JsonArray jsonArray = jsonElement.getAsJsonArray();
                    bodyList = new ArrayList<>();
                    for (com.google.gson.JsonElement element : jsonArray) {
                        com.google.gson.JsonObject jsonObject = element.getAsJsonObject();
                        HashMap<String, Object> map = new HashMap<>();
                        for (String key : jsonObject.keySet()) {
                            com.google.gson.JsonElement valueElement = jsonObject.get(key);
                            if (!valueElement.isJsonNull()) {
                                map.put(key, valueElement.getAsString());
                            }
                        }
                        bodyList.add(map);
                    }
                } else if (jsonElement.isJsonObject()) {
                    singleBody = new HashMap<>();
                    com.google.gson.JsonObject jsonObject = jsonElement.getAsJsonObject();
                    for (String key : jsonObject.keySet()) {
                        com.google.gson.JsonElement valueElement = jsonObject.get(key);
                        if (!valueElement.isJsonNull()) {
                            singleBody.put(key, valueElement.getAsString());
                        }
                    }
                }
            } catch (Exception e) {
                singleBody = new HashMap<>();
                singleBody.put("error", "Ошибка при парсинге JSON тела запроса");
            }
        }
    }

    private static void processSingleObject(HashMap<String, Object> body) {
        if (body.get("xVal") == null || body.get("yVal") == null || body.get("rVal") == null) {
            sendError("Missing parameters: xVal, yVal or rVal");
        }
            double x = Double.parseDouble(String.valueOf(body.get("xVal")));
            double y = Double.parseDouble(String.valueOf(body.get("yVal")));
            double r = Double.parseDouble(String.valueOf(body.get("rVal")));
    }



    private static void sendJsonResponse(String jsonResponse) {
        System.out.println("Content-Type: application/json\n\n");
        System.out.println(jsonResponse);
    }

    private static String renderJson(List<Map<String, String>> results) {
        StringBuilder jsonResponse = new StringBuilder("[");
        for (Map<String, String> result : results) {
            jsonResponse.append("{")
                    .append("\"x\":\"").append(result.get("x")).append("\",")
                    .append("\"y\":\"").append(result.get("y")).append("\",")
                    .append("\"r\":\"").append(result.get("r")).append("\",")
                    .append("\"coordsStatus\":\"").append(result.get("coordsStatus")).append("\",")
                    .append("\"currentTime\":\"").append(result.get("currentTime")).append("\",")
                    .append("\"benchmarkTime\":\"").append(result.get("benchmarkTime")).append("\"")
                    .append("},");
        }
        if (jsonResponse.length() > 1) {
            jsonResponse.deleteCharAt(jsonResponse.length() - 1);
        }
        jsonResponse.append("]");
        return jsonResponse.toString();
    }
}
