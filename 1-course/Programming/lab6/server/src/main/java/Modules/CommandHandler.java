package Modules;


import CollectionObject.RouteModel;
import Exceptions.NonExistingElementException;
import Network.Response;

import java.util.LinkedList;


public class CommandHandler {
    private CollectionService collectionService;
    private static CSVProvider csvProvider;
    private static LinkedList<String> commandHistory = new LinkedList<>();

    public CommandHandler() {
        this.collectionService = new CollectionService();
        csvProvider = new CSVProvider(CSVProvider.COLLECTION_PATH);
    }

    public Response help(String strArgument, RouteModel objArgument){
        if (!strArgument.isBlank() || objArgument != null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            String message =
                    """
                    Список доступных команд:
                    ================================================================================================
                    help - справка по командам
                    info - вывод данных о коллекции (тип, дата инициализации, количество элементов)
                    show - вывести все элементы коллекции
                    add <el> - добавить элемент в коллекцию
                    update <id> <el> - обновить id элемента на заданный
                    removeById <id> - удалить элемент по id
                    clear - очистить всю коллекцию
                    executeScript <path> - исполнить скрипт
                    exit - завершить работу клиентского приложения
                    removeGreater <id> - удалить из коллекции все элементы, превышающие данный по id
                    head - отсортировать коллекцию в обратном порядке
                    history - вывести последние 7 команд
                    averageOfDistance - вывод дистанции всех элементов
                    countByDistance <distance> - вывести количество элементов коллекции, мощность которых выше заданной
                    filterGreaterThanDistance <distance> - вывести все элементы, дистанция которых превышает заданную
                    ================================================================================================
                    """
                    ;
            return new Response(message, "");
        }
    }

    public Response info(String strArgument, RouteModel objArgument){
        if (!strArgument.isBlank() || objArgument != null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            var message = collectionService.info();
            return new Response(message, "");
        }
    }

    public Response show(String strArgument, RouteModel objArgument){
        if (!strArgument.isBlank() || objArgument != null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            var collection = collectionService.show();

            if (collection.isEmpty()){
                return new Response("В коллекции пока нету ни одного элемента", "");
            } else{
                return new Response("Коллекция успешно распечатана", collection.toString());
            }
        }
    }

    public Response add(String strArgument, RouteModel objArgument){
        if (!strArgument.isBlank() && objArgument == null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            var collection = collectionService.add(objArgument);
            return new Response("Элемент успешно добавлен", collection.toString());
        }
    }

    public Response update(String strArgument, RouteModel objArgument){
        if (strArgument.isBlank() && objArgument == null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            try {
                long current_id = Long.parseLong(strArgument);

                if (current_id > 0){
                    var collection = collectionService.update(current_id, objArgument);
                    return new Response("элемент c id " + current_id + " успешно обновлён", collection.toString());

                } else {
                    return new Response("id не может быть отрицательным", "" );
                }

            } catch (NumberFormatException e){
                return new Response("Неверный формат аргументов", "" );
            } catch (NonExistingElementException e) {
                return new Response(e.getMessage(), "");
            }
        }
    }

    public Response removeById(String strArgument, RouteModel objArgument){
        if (strArgument.isBlank() || objArgument != null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            try {
                long id = Long.parseLong(strArgument);

                if (id > 0){
                    var collection = collectionService.removeById(id);
                    return new Response("Элемент с id " + id + " успешно удалён", collection.toString());
                } else {
                    return new Response("id не может быть отрицательным", "" );
                }

            } catch (NumberFormatException e){
                return new Response("Неверный формат аргументов", "" );
            } catch (NonExistingElementException e) {
                return new Response(e.getMessage(), "");
            }
        }
    }

    public Response clear(String strArgument, RouteModel objArgument){
        if (!strArgument.isBlank() || objArgument != null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            var collection = collectionService.clear();
            return new Response("коллекция успешно очищена", collection.toString());
        }
    }

    public static void save(){
        csvProvider.save(CollectionService.collection);
    }

    public Response removeGreater(String strArgument, RouteModel objArgument){
        if (strArgument.isBlank() || objArgument != null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            try {
                long startId = Long.parseLong(strArgument) + 1;

                if (startId > 0) {
                    var collection = collectionService.removeGreater(startId);
                    return new Response("элементы успешно удалены", collection.toString());

                } else {
                    return new Response("id не может быть отрицательным", "");
                }

            } catch (NumberFormatException e){
                return new Response("Неверный формат аргументов", "" );
            } catch (NonExistingElementException e) {
                return new Response(e.getMessage(), "");
            }
        }
    }

    public Response head(String strArgument, RouteModel objArgument){
        if (!strArgument.isBlank() || objArgument != null){
            return new Response("Неверные аргументы команды", "" );
        }
        var collection = collectionService.head();
        return new Response("элементы успешно отсортированы по убыванию", collection.toString());
    }

    public Response history(String strArgument, RouteModel objArgument){
        StringBuilder historyList = new StringBuilder();

        if (!strArgument.isBlank() || objArgument != null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            for (String command : commandHistory) {
                historyList.append(command).append("\n");
            }
        }
        return new Response("Последние 7 команд, введённые пользователем: \n" + historyList, "" );
    }

    public Response averageOfDistance(String strArgument, RouteModel objArgument){
        if (!strArgument.isBlank() || objArgument != null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            return new Response("Средняя дистанция: " + collectionService.averageOfDistance(), "");
        }
    }

    public Response countByDistance(String strArgument, RouteModel objArgument){
        if (strArgument.isBlank() || objArgument != null){
            return new Response("Неверные аргументы команды", "" );
        } else {
            try {
                int distance = Integer.parseInt(strArgument);
                var count = collectionService.countByDistance(distance);
                    return new Response("Количество маршрутов с дистанцией равной заданной:  " + count + ":   " + count, "" );
            } catch (NumberFormatException e){
                return new Response("Неверный формат аргументов", "" );
            }
        }
    }

    public Response filterGreaterThanDistance(String strArgument, RouteModel objArgument){
        if (strArgument.isBlank() || objArgument != null){
            return new Response("Неверные аргументы команды", "" );
        }
        try{
            int distance = Integer.parseInt(strArgument);
            var collection = collectionService.filterGreaterThanDistance(distance);
            return new Response("Коллекция больше заданного числа: ", collection.toString());
        } catch (NumberFormatException e){
            return new Response("Неверный формат аргументов", "" );
        }
    }


    public static void addCommand(String command){
        if (commandHistory.size() == 7){
            commandHistory.removeFirst();
        }
        commandHistory.addLast(command);
    }
}