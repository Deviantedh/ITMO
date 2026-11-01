package Moves;
import ru.ifmo.se.pokemon.*;
public class ThunderWave extends StatusMove{
    public ThunderWave(){super (Type.ELECTRIC, 0, 90) ;}
    protected String describe(){return "использует Thunder Wave";}
    protected void applyOppEffects(Pokemon var1) {
        Effect.paralyze(var1);
    }
}
