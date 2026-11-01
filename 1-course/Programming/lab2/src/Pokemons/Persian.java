package Pokemons;
import Moves.PlayRough;
public class Persian extends Meowth{
    public Persian(String name, int level) {
        super(name, level);
        setStats(65, 70, 60, 65, 65, 115);
        addMove(new PlayRough());
    }
}
