package Pokemons;
import Moves.*;
import ru.ifmo.se.pokemon.*;
public class Kyogre extends Pokemon{
    public Kyogre (String name, int level) {
        super(name, level);
        setStats(100, 100, 90, 150,140, 90);
        setType(Type.WATER);
        setMove(new WaterPulse(), new DoubleTeam(), new Facade(), new WorkUp());
    }
}
