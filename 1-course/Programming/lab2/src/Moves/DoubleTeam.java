package Moves;
import ru.ifmo.se.pokemon.*;
public class DoubleTeam extends StatusMove{
    public DoubleTeam(){super (Type.NORMAL, 0, 100) ;}
    protected String describe(){return "использует Double Team";}
    protected void applySelfEffects(Pokemon att) {
        att.setCondition(new Effect().stat(Stat.EVASION, 1));
    }
}
