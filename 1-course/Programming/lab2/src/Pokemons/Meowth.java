package Pokemons;

import Moves.ShadowBall;
import Moves.ShadowClaw;
import Moves.WorkUp;
import ru.ifmo.se.pokemon.*;
public class Meowth extends Pokemon {
    public Meowth(String name, int level) {
        super(name, level);
        setStats(40, 45, 35, 40, 40, 90);
        setType(Type.NORMAL);
        setMove(new WorkUp(), new ShadowBall(), new ShadowClaw());
    }
}
