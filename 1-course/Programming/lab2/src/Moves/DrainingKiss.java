package Moves;
import ru.ifmo.se.pokemon.*;
public class DrainingKiss extends PhysicalMove{
    public DrainingKiss(){super (Type.FAIRY, 50, 100) ;}
    protected String describe(){return "использует Draining Kiss";}
    protected void applyOppDamage(Pokemon var1, double damage) {
        //наносит урон
        var1.setMod(Stat.HP, (int) Math.round(damage));
    }
    protected void applySelfDamage(Pokemon var1, double var2) {
        //восстановление
        var1.setMod(Stat.HP, -(int) Math.round(var2*0.75));
    }
}
