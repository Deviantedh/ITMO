package Moves;
import ru.ifmo.se.pokemon.*;
public class ShadowBall extends SpecialMove{
    public ShadowBall(){super (Type.GHOST, 80, 100) ;}
    protected String describe(){return "использует Shadow Ball";}
    protected void applyOppDamage(Pokemon var1, double damage) {
        var1.setMod(Stat.HP, (int) Math.round(damage));
        if (Math.random() <= 0.2) var1.setCondition(new Effect().stat(Stat.SPECIAL_DEFENSE, -1));
    }
}
