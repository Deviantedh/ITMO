package Moves;
import ru.ifmo.se.pokemon.*;
public class ShadowClaw extends PhysicalMove{
    public ShadowClaw(){super (Type.GHOST, 70, 100) ;}
    protected String describe(){return "использует Shadow Claw";}
    protected double calcCriticalHit(Pokemon att, Pokemon def){
        if (Math.random() <= 1/8) return 2;
        else return 1;
    }
}
