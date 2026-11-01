package Network;

import java.io.Serializable;

public class Response implements Serializable {
    private String message;
    private String collectionToStr;
//заменить на сериализацию самой коллекции
    public Response(String message, String collection) {
        this.message = message;
        this.collectionToStr = collection;
    }

    public String getMessage() {
        return message;
    }

    public String getCollection() {
        return collectionToStr;
    }
}
