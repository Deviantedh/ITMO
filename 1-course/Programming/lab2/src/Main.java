import Pokemons.*;
import ru.ifmo.se.pokemon.*;

public class Main {
    public static void main(String[] args) {
        Battle b = new Battle();
        Pokemon p1 = new Kyogre("Скилёвий", 45);
        Pokemon p2 = new Meowth("Котек", 80);
        Pokemon p3 = new Persian("Кошкообразное", 70);
        Pokemon p4 = new Ralts("Бедни", 100);
        Pokemon p5 = new Kirlia("Баллора", 100);
        Pokemon p6 = new Gallade("Забивной", 100);
        b.addAlly(p1);
        b.addAlly(p2);
        b.addAlly(p3);
        b.addFoe(p4);
        b.addFoe(p5);
        b.addFoe(p6);
        b.go();

    }
}