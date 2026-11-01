package Moves;
import ru.ifmo.se.pokemon.*;
public class
Confide extends StatusMove{
    public Confide(){super (Type.NORMAL, 0, 100) ;}
    protected String describe(){return "использует Confide";}
    protected void applyOppEffects(Pokemon var1) {
        var1.setCondition(new Effect().stat(Stat.SPECIAL_ATTACK, -1));
    }
}
