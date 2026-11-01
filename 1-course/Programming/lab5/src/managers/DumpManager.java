package managers;

import models.Route;
import utility.console.Console;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;

import java.util.Collection;
import java.util.PriorityQueue;

public class DumpManager {
  private final String fileName;
  private final Console console;

  public DumpManager(String fileName, Console console) {
    if (!(new File(fileName).exists())) {
      fileName = "../" + fileName;
    }
    this.fileName = fileName;
    this.console = console;
  }

  public void writeCollection(Collection<Route> collection) {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(Route.class, PriorityQueue.class);
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.marshal(collection, new File(fileName));
      console.println("Коллекция успешно сохранена в файл!");
    } catch (JAXBException e) {
      console.printError("Ошибка при сохранении коллекции в файл!");
    }
  }

  public Collection<Route> readCollection() {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(Route.class, PriorityQueue.class);
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      Collection<Route> collection = (Collection<Route>) unmarshaller.unmarshal(new File(fileName));
      console.println("Коллекция успешно загружена из файла!");
      return collection;
    } catch (JAXBException e) {
      console.printError("Ошибка при загрузке коллекции из файла!");
    }
    return new PriorityQueue<>();
  }
}
