package Pokemons;
import Moves.DrainingKiss;
import ru.ifmo.se.pokemon.Type;
public class Kirlia extends Ralts {
    public Kirlia(String name, int level) {
        super(name, level);
        setStats(38, 35, 35, 65, 55, 50);
        setType(Type.PSYCHIC, Type.FAIRY);
        addMove(new DrainingKiss());

    }
}
