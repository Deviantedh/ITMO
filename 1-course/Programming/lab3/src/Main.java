import Actors.Human;

public class Main {
    public static void main(String[] args) {
        //adding our objects
        Human Danny = new Human("Дэнни");
        Termometer term = new Termometer("Термометр");
        Shlang shlang = new Shlang("Шланг");
        //starting to telling a story
        Danny.action.action("обтёр губы рукой, повторяя отцовский жест.");
        Danny.moves.shag();
        shlang.looks.Looks("лежит... не двигается...");
        Danny.moves.shag();
        Danny.think.doing("про себя: «Ну, видишь, какой ты глупый? Думал про ту комнату и про дурацкую сказку о Синей Бороде и завелся, а шланг, наверное, собирался свалиться уже лет пять. Вот и все.»", "Смотря на шланг, ");
        Danny.think.doing("про ос", "Пристально глядя на пол, на шланг, ");
        shlang.looks.Looks(", а именно его наконечник блестел на фоне коврика.");
        Danny.think.doing("", ", что Шланг, будто говорит ему: ");

    }

}