package Moves;
import ru.ifmo.se.pokemon.*;
public class WorkUp extends StatusMove{
    public WorkUp(){super (Type.NORMAL, 0, 100) ;}
    protected String describe(){return "использует Work Up";}
    protected void applySelfEffects(Pokemon att) {
        att.setCondition(new Effect().stat(Stat.ATTACK, 1));
        att.setCondition(new Effect().stat(Stat.SPECIAL_ATTACK, 1));

    }
}
