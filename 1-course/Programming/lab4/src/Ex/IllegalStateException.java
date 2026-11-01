package Ex;

import Actors.Device;
import Actors.Human;

public class IllegalStateException extends RuntimeException {
    private String a;
    public Human objh;
    public Device objd;
    public boolean b;
    public String s;

    public IllegalStateException(String a, Human objh, Device objd, boolean b){
        this.a= a;
        this.objh = objh;
        this.objd = objd;
        this.b = b;
    }
    public String getMessage(String a, Human objh, Device objd, boolean b){
    if(b){
        s = objh.getName() + a + "включить " + objd;}
    else{
        s = objh.getName() + a + "выключить " + objd;}
    return s;
    }
}
