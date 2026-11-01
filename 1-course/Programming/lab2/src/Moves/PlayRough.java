package Moves;
import ru.ifmo.se.pokemon.*;
public class PlayRough extends SpecialMove{
    public PlayRough(){super (Type.FAIRY, 90, 90) ;}
    protected String describe(){return "использует Play Rough";}
    protected void applyOppDamage(Pokemon var1, double damage) {
        var1.setMod(Stat.HP, (int) Math.round(damage));
        if (Math.random() <= 0.1) var1.setCondition(new Effect().stat(Stat.SPECIAL_ATTACK, -1));
    }
}
