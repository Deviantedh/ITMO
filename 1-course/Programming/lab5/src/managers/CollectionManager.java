package managers;

import models.Location;
import models.Route;
import utility.console.Console;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class CollectionManager {
  private LinkedList<Route> collection = new LinkedList<>();
  private LocalDateTime lastInitTime;
  private LocalDateTime lastSaveTime;
  private final DumpManager dumpManager;

  public CollectionManager(DumpManager dumpManager) {
    this.lastInitTime = null;
    this.lastSaveTime = null;
    this.dumpManager = dumpManager;

    loadCollection();
  }

  public void validateAll(Console console) {
    Location.allLocations().values().forEach(location -> {
      if (!location.validate()) {
        console.printError("Организация с id=" + location.getId() + " имеет невалидные поля.");
      }
    });

    (new ArrayList<>(this.getCollection())).forEach(product -> {
      if (!product.validate()) {
        console.printError("Продукт с id=" + location.getId() + " имеет невалидные поля.");
      }
    });
    console.println("! Загруженные продукты валидны.");
  }

  /**
   * @return коллекция.
   */
  public Queue<Route> getCollection() {
    return collection;
  }

  /**
   * @return Последнее время инициализации.
   */
  public LocalDateTime getLastInitTime() {
    return lastInitTime;
  }

  /**
   * @return Последнее время сохранения.
   */
  public LocalDateTime getLastSaveTime() {
    return lastSaveTime;
  }

  /**
   * @return Имя типа коллекции.
   */
  public String collectionType() {
    return collection.getClass().getName();
  }

  /**
   * @return Размер коллекции.
   */
  public int collectionSize() {
    return collection.size();
  }

  /**
   * @return Первый элемент коллекции (null если коллекция пустая).
   */
  public Route getFirst() {
    if (collection.isEmpty()) return null;
    return collection.peek();
  }

  /**
   * @return Последний элемент коллекции (null если коллекция пустая).
   */
  public Route getLast() {
    if (collection.isEmpty()) return null;
    return collection.getLast();
  }

  /**
   * @param id ID элемента.
   * @return Элемент по его ID или null, если не найдено.
   */
  public Route getById(int id) {
    for (Route element : collection) {
      if (element.getId() == id) return element;
    }
    return null;
  }

  public boolean checkExist(int id) {
    for (Route element : collection) {
      if (element.getId() == id) return true;
    }
    return false;
  }

  public Route getByValue(Route elementToFind) {
    for (Route element : collection) {
      if (element.equals(elementToFind)) return element;
    }
    return null;
  }

  public void addToCollection(Route element) {
    collection.add(element);
    Route.touchNextId();
  }

  public void removeFromCollection(Route element) {
    collection.remove(element);
  }


  public void clearCollection() {
    collection.clear();
  }

  public void saveCollection() {
    dumpManager.writeCollection(collection);
    lastSaveTime = LocalDateTime.now();
  }

  private void loadCollection() {
    collection = new LinkedList<>(dumpManager.readCollection());
    lastInitTime = LocalDateTime.now();
  }

  @Override
  public String toString() {
    if (collection.isEmpty()) return "Коллекция пуста!";
    var last = getLast();

    StringBuilder info = new StringBuilder();
    for (Route route : collection) {
      info.append(route);
      if (route != last) info.append("\n\n");
    }
    return info.toString();
  }
}
