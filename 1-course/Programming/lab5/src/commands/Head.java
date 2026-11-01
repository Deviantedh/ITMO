package commands;

import  managers.CollectionManager;
import  utility.console.Console;

public class Head extends Command {
  private final Console console;
  private final CollectionManager collectionManager;

  public Head(Console console, CollectionManager collectionManager) {
    super("head", "вывести первый элемент коллекции");
    this.console = console;
    this.collectionManager = collectionManager;
  }

  @Override
  public boolean apply(String[] arguments) {
    if (!arguments[1].isEmpty()) {
      console.println("Использование: '" + getName() + "'");
      return false;
    }

    if (collectionManager.getCollection().isEmpty()) {
      console.println("Коллекция пуста!");
    } else {
      console.println(collectionManager.getFirst());
    }

    return true;
  }
}
