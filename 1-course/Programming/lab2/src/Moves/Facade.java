package Moves;
import ru.ifmo.se.pokemon.*;
public class Facade extends PhysicalMove{
    public Facade(){super (Type.NORMAL, 70, 100) ;}
    protected String describe(){return "использует Facade";}
    protected void applyOppDamage(Pokemon var1, double damage) {
        if (var1.getCondition()==Status.BURN || var1.getCondition()==Status.POISON || var1.getCondition()==Status.PARALYZE) {
            var1.setMod(Stat.HP, (int) Math.round(damage*2));
        } else {
            var1.setMod(Stat.HP, (int) Math.round(damage));
        }
    }
}
