package commands;

import commands.Command;
import  exceptions.IncorrectInputInScriptException;
import  exceptions.InvalidFormException;
import  exceptions.WrongAmountOfElementsException;
import  managers.CollectionManager;
import models.forms.RouteForm;
import  utility.console.Console;


public class Add extends Command {
  private final Console console;
  private final CollectionManager collectionManager;

  public Add(Console console, CollectionManager collectionManager) {
    super("add {element}", "добавить новый элемент в коллекцию");
    this.console = console;
    this.collectionManager = collectionManager;
  }


  @Override
  public boolean apply(String[] arguments) {
    try {
      if (!arguments[1].isEmpty()) throw new WrongAmountOfElementsException();
      console.println("* Создание нового продукта:");
      collectionManager.addToCollection((new RouteForm(console, collectionManager)).build());
      console.println("Продукт успешно добавлен!");
      return true;

    } catch (WrongAmountOfElementsException exception) {
      console.printError("Неправильное количество аргументов!");
      console.println("Использование: '" + getName() + "'");
    } catch (InvalidFormException exception) {
      console.printError("Поля продукта не валидны! Продукт не создан!");
    } catch (IncorrectInputInScriptException ignored) {}
    return false;
  }

  @Override
  public void execute(String[] args) {

  }
}
