package src.Utils;

import java.util.UUID;

public class TokenGenerator {
    public String getNewToken(){
        return UUID.randomUUID().toString();
    }
}