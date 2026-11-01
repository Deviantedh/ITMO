package Moves;
import ru.ifmo.se.pokemon.*;
public class WaterPulse extends PhysicalMove{
    public WaterPulse(){super (Type.WATER, 60, 100) ;}
    protected String describe(){return "использует Water Pulse";}
    protected void applyOppDamage(Pokemon var1, double damage) {
        var1.setMod(Stat.HP, (int) Math.round(damage));
        if (Math.random() <= 0.2) Effect.confuse(var1);
    }
}
