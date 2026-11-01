package Modules;

import CollectionObject.Coordinates;
import CollectionObject.Location;
import CollectionObject.RouteModel;
import CollectionObject.Route;
import Exceptions.EmptyFieldException;
import Exceptions.NegativeFieldException;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Stack;


public class CSVProvider implements DataProvider {
    protected static Path COLLECTION_PATH;
    private long maxId = 0L;
    private Stack<Route> stack = CollectionService.collection;

    public CSVProvider(Path collectionPath) {
        COLLECTION_PATH = collectionPath;
    }

    @Override
    public void save(Stack<Route> collection) {
        File collectionFile = new File(String.valueOf(COLLECTION_PATH));

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(collectionFile));

            byte[] header = (
                    "id" + "," +
                            "Название" + "," +
                            "Координата x" + "," +
                            "Координата y" + "," +
                            "Дата создания" + "," +
                            "Откуда координата x" + "," +
                            "Откуда координата y" + "," +
                            "Откуда координата z" + "," +
                            "Из" + "," +
                            "Куда координата x" + "," +
                            "Куда координата y" + "," +
                            "Куда координата z" + "," +
                            "В" + "," +
                            "Дистанция" + "\n"
            ).getBytes(StandardCharsets.UTF_8);

            bos.write(header);
            saveToCSV(collection, bos);
            bos.flush();
            bos.close();

            System.out.println("Коллекция успешно сохранена");

        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода");
        }
    }

    @Override
    public void load() {
        File collectionFile = new File(String.valueOf(COLLECTION_PATH));

        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(collectionFile));
            CSVParser parser = new CSVParserBuilder().withSeparator(',').build();
            CSVReader csvReader = new CSVReaderBuilder(fileReader).withCSVParser(parser).withSkipLines(1).build();

            List<String[]> lines = csvReader.readAll();

            try {
                for (String[] line : lines) {
                    try {
                        Route el = new Route();
                        Coordinates coordinates = new Coordinates();
                        Location from = new Location();
                        Location to = new Location();

                        var id = askId(line[0]);
                        if (id > maxId) {
                            maxId = id;
                        }
                        el.setId(id);
                        CollectionService.elementsCount = maxId;

                        var name = askString(line[1]);
                        el.setName(name);

                        var x = askX(line[2]);
                        var y = askY(line[3]);
                        coordinates.setX(x);
                        coordinates.setY(y);
                        el.setCoordinates(coordinates);

                        ZonedDateTime date = ZonedDateTime.parse(line[4]);
                        el.setCreationDate(date);

                        var lFX = askFloat(line[5]);
                        var lFY = askLong(line[6]);
                        var lFZ = askDouble(line[7]);
                        var lFName = askString(line[8]);
                        from.setLX(lFX);
                        from.setLY(lFY);
                        from.setLZ(lFZ);
                        from.setName(lFName);
                        el.setFrom(from);

                        var lTX = askFloat(line[9]);
                        var lTY = askLong(line[10]);
                        var lTZ = askDouble(line[11]);
                        var lTName = askString(line[12]);
                        to.setLX(lTX);
                        to.setLY(lTY);
                        to.setLZ(lTZ);
                        to.setName(lTName);
                        el.setTo(to);

                        var distance = askInt(line[13]);
                        el.setDistance(distance);

                        stack.add(el);
                        System.out.println("Элемент " + id + " был успешно загружен");
                    } catch (Exception e) {
                        System.out.println("Ошибка формата числа. Проверьте разделитель в файле коллекции");
                        maxId += 1;
                    }

                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка формата числа. Проверьте разделитель в файле коллекции");
                System.exit(1);
            }
            /* catch (ParseException e) {
                System.out.println("Ошибка конвертации объекта. Проверьте файл");
                System.exit(1);
            } */
            /*catch (EmptyFieldException | NegativeFieldException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }*/
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода. Проверьте путь или имя файла");
            System.exit(1);
        } catch (CsvException e) {
            System.out.println("Невалидный файл коллекции. Проверьте разделители");
            System.exit(1);
        }
    }

    private void saveToCSV(Stack<Route> stack, BufferedOutputStream bos) throws IOException {
        for (Route route : stack) {
            byte[] id = (route.getId() + ",").getBytes(StandardCharsets.UTF_8);
            byte[] name = (route.getName() + ",").getBytes(StandardCharsets.UTF_8);
            byte[] x = (route.getCoordinates().getX() + ",").getBytes(StandardCharsets.UTF_8);
            byte[] y = (route.getCoordinates().getY() + ",").getBytes(StandardCharsets.UTF_8);
            byte[] creationDate = (route.getCreationDate() + ",").getBytes(StandardCharsets.UTF_8);
            byte[] lFX = (route.getFrom().getLX() + ",").getBytes(StandardCharsets.UTF_8);
            byte[] lFY = (route.getFrom().getLY() + ",").getBytes(StandardCharsets.UTF_8);
            byte[] lFZ = (route.getFrom().getLZ() + ",").getBytes(StandardCharsets.UTF_8);
            byte[] lFName = (route.getFrom().getName() + ",").getBytes(StandardCharsets.UTF_8);
            byte[] lTX = (route.getTo().getLX() + ",").getBytes(StandardCharsets.UTF_8);
            byte[] lTY = (route.getTo().getLY() + ",").getBytes(StandardCharsets.UTF_8);
            byte[] lTZ = (route.getTo().getLZ() + ",").getBytes(StandardCharsets.UTF_8);
            byte[] lTName = (route.getTo().getName() + ",").getBytes(StandardCharsets.UTF_8);
            byte[] distance = (route.getDistance() + ",").getBytes(StandardCharsets.UTF_8);

            bos.write(id);
            bos.write(name);
            bos.write(x);
            bos.write(y);
            bos.write(creationDate);
            bos.write(lFX);
            bos.write(lFY);
            bos.write(lFZ);
            bos.write(lFName);
            bos.write(lTX);
            bos.write(lTY);
            bos.write(lTZ);
            bos.write(lTName);
            bos.write(distance);

            bos.write("\n".getBytes(StandardCharsets.UTF_8));
        }
    }

    private Long askId(String num) throws NegativeFieldException {
        long id = Long.parseLong(num);
        if (id <= 0) {
            throw new NegativeFieldException("id должен быть больше нуля");
        }
        return id;
    }

    private String askString(String name) throws EmptyFieldException {
        if (name.isEmpty()) {
            throw new EmptyFieldException("Это поле не может быть пустым. Проверьте файл");
        }
        return name.trim();
    }

    private float askX(String num) throws NumberFormatException {
        return Float.parseFloat(num);
    }

    private float askY(String num) throws NumberFormatException {
        return Float.parseFloat(num);
    }

    private float askFloat(String n) throws NumberFormatException, NegativeFieldException {
        float num = Float.parseFloat(n);
        if (num > 0) {
            return num;
        } else {
            throw new NegativeFieldException("Число в этом поле не может быть отрицательным");
        }
    }

    private double askDouble(String n) throws NumberFormatException, NegativeFieldException {
        double num = Double.parseDouble(n);
        if (num > 0) {
            return num;
        } else {
            throw new NegativeFieldException("Число в этом поле не может быть отрицательным");
        }
    }

    private long askLong(String n) throws NumberFormatException, NegativeFieldException {
        long num = Long.parseLong(n);
        if (num > 0) {
            return num;
        } else {
            throw new NegativeFieldException("Число в этом поле не может быть отрицательным");
        }
    }

    private int askInt(String n) throws NumberFormatException, NegativeFieldException {
        int num = Integer.parseInt(n);
        if (num > 0) {
            return num;
        } else {
            throw new NegativeFieldException("Число в этом поле не может быть отрицательным");
        }
    }
}

