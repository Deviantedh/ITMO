package Moves;
import ru.ifmo.se.pokemon.*;
public class CalmMind extends StatusMove{
    public CalmMind(){super (Type.PSYCHIC, 0, 100) ;}
    protected String describe(){return "использует Calm Mind";}
    protected void applyOppEffects(Pokemon var1) {
        var1.setCondition(new Effect().stat(Stat.SPECIAL_ATTACK, 1));
        var1.setCondition(new Effect().stat(Stat.SPECIAL_DEFENSE, 1));
    }
}
