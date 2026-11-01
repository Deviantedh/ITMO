package Pokemons;
import Moves.CalmMind;
import ru.ifmo.se.pokemon.Type;

public class Gallade extends Kirlia {
    public Gallade(String name, int level) {
        super(name, level);
        setStats(68, 125, 65, 65, 115, 80);
        setType(Type.PSYCHIC, Type.FIGHTING);
        addMove(new CalmMind());
    }
}
