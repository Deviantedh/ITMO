import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

public class Request {
    private final HashMap<String, Object> parameters;
    private HashMap<String, Object> singleBody;       // Для одиночного объекта
    private List<HashMap<String, Object>> bodyList;   // Для списка объектов

    public Request() {
        this.parameters = new HashMap<>();
        this.singleBody = null;
        this.bodyList = null;
    }

    public Request fillParameters(Properties params) {
        String queryString = params.getProperty("QUERY_STRING");
        if (queryString != null && !queryString.isEmpty()) {
            for (String pair : queryString.split("&")) {
                String[] keyValue = pair.split("=");
                if (keyValue.length > 1) {
                    parameters.put(keyValue[0], keyValue[1]);
                } else {
                    parameters.put(keyValue[0], "");
                }
            }
        }
        return this;
    }

    public Request fillBody(Properties params) {
        String contentLength = params.getProperty("CONTENT_LENGTH");
        int length = (contentLength != null) ? Integer.parseInt(contentLength) : 0;

        if (length > 0) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                char[] bodyChars = new char[length];
                reader.read(bodyChars, 0, length);
                String requestBody = new String(bodyChars);
                parseBody(requestBody);
            } catch (Exception e) {
                singleBody = new HashMap<>();
                singleBody.put("error", "Ошибка при чтении тела запроса");
            }
        }
        return this;
    }

    private void parseBody(String requestBody) {
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


    public HashMap<String, Object> getSingleBody() {
        return singleBody;
    }

    public List<HashMap<String, Object>> getBodyList() {
        return bodyList;
    }

    public HashMap<String, Object> getParameters() {
        return parameters;
    }
}
