package Moves;
import ru.ifmo.se.pokemon.*;
public class Swagger extends StatusMove{
    public Swagger(){super (Type.NORMAL, 0, 85) ;}
    protected String describe(){return "использует Swagger";}
    protected void applySelfEffects(Pokemon var1) {
        Effect.confuse(var1);
        var1.setCondition(new Effect().stat(Stat.ATTACK, 2));
    }
}
