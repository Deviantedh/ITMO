package Modules;


import CollectionObject.Route;
import CollectionObject.RouteModel;
import CollectionObject.Location;
import Exceptions.DBProviderException;
import Exceptions.NonExistingElementException;
import Network.Response;
import Network.User;

import java.util.LinkedList;
import java.util.Stack;


public class CommandHandler {
    private CollectionService collectionService;
    private static LinkedList<String> commandHistory = new LinkedList<>();

    public CommandHandler() {
        this.collectionService = new CollectionService();
    }

    public Response help(User user, String strArgument, RouteModel objArgument) {
        if (!strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

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
                            averageOfDistance - вывод средней дистанции всех элементов
                            countByDistance <distance> - вывести количество элементов коллекции, мощность которых выше заданной
                            filterGreaterThanDistance <distance> - вывести все элементы, дистанция которых превышает заданную
                            ================================================================================================
                            """;
            return new Response(message, "");
        }
    }

    public Response info(User user, String strArgument, RouteModel objArgument) {
        if (!strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            var message = collectionService.info();
            return new Response(message, "");
        }
    }

    public Response add(User user, String strArgument, RouteModel objArgument) {
        if (!strArgument.isBlank() && objArgument == null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            Stack<Route> collection;
            try {
                collection = collectionService.add(objArgument);
            } catch (DBProviderException e) {
                return new Response(e.getMessage(), "");
            }
            return new Response("Элемент успешно добавлен", collection.toString());
        }
    }

    public Response update(User user, String strArgument, RouteModel objArgument) {
        if (strArgument.isBlank() && objArgument == null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            try {
                long current_id = Long.parseLong(strArgument);

                if (current_id > 0) {

                    if (CollectionService.collection.stream().anyMatch(route -> route.getId() == current_id)) {

                        if (CollectionService.collection.stream().anyMatch(route -> route.getId() == current_id
                                && route.getCreator().equals(user.getUsername()))) {

                            var collection = collectionService.update(user, current_id, objArgument);
                            return new Response("элемент c id " + current_id + " успешно обновлён", collection.toString());
                        }
                        return new Response("Вы не можете изменить этот объект", "");

                    }
                    return new Response("Элемента с таким id не существует", "");

                } else {
                    return new Response("id не может быть отрицательным", "");
                }

            } catch (NumberFormatException e) {
                return new Response("Неверный формат аргументов", "");
            } catch (DBProviderException e) {
                return new Response(e.getMessage(), "");
            }
        }
    }

    public Response removeById(User user, String strArgument, RouteModel objArgument) {
        if (strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            try {
                long id = Long.parseLong(strArgument);

                if (id > 0) {
                    if (CollectionService.collection.stream().anyMatch(route -> route.getId() == id)) {
                        if (CollectionService.collection.stream().anyMatch(route -> route.getId() == id
                                && route.getCreator().equals(user.getUsername()))) {

                            var collection = collectionService.removeById(user, id);
                            return new Response("Элемент с id " + id + " успешно удалён", collection.toString());
                        }
                        return new Response("Вы не можете удалить этот объект", "");
                    }
                    return new Response("Элемента с таким id не существует", "");
                }
                return new Response("id не может быть отрицательным", "");

            } catch (NumberFormatException e) {
                return new Response("Неверный формат аргументов", "");
            } catch (DBProviderException e) {
                return new Response(e.getMessage(), "");
            }
        }
    }

    public Response clear(User user, String strArgument, RouteModel objArgument) {
        if (!strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            Stack<Route> collection;
            try {
                collection = collectionService.clear(user);
            } catch (DBProviderException e) {
                return new Response(e.getMessage(), "");
            }
            return new Response("коллекция успешно очищена", collection.toString());
        }
    }

    public Response removeGreater(User user, String strArgument, RouteModel objArgument) {
        if (strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            try {
                long startId = Long.parseLong(strArgument) + 1;

                if (startId > 0) {
                    var collection = collectionService.removeGreater(user, startId);
                    return new Response("элементы успешно удалены", collection.toString());

                } else {
                    return new Response("id не может быть отрицательным", "");
                }

            } catch (NumberFormatException e) {
                return new Response("Неверный формат аргументов", "");
            } catch (NonExistingElementException | DBProviderException e) {
                return new Response(e.getMessage(), "");
            }
        }
    }

    public Response head(User user, String strArgument, RouteModel objArgument) {
        if (!strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            var collection = collectionService.head();
            return new Response("Показан первый элемент", collection.toString());
        }
    }

    public Response show(User user, String strArgument, RouteModel objArgument) {
        if (!strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            var collection = collectionService.show();

            if (collection.isEmpty()) {
                return new Response("В коллекции пока нету ни одного элемента", "");
            } else {
                return new Response("Коллекция успешно распечатана", collection.toString());
            }
        }
    }

    public Response history(User user, String strArgument, RouteModel objArgument) {
        StringBuilder historyList = new StringBuilder();

        if (!strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            for (String command : commandHistory) {
                historyList.append(command).append("\n");
            }
        }
        return new Response("Последние 7 команд, введённые пользователем: \n" + historyList, "");
    }

//    public Response averageOfDistance(User user, String strArgument, RouteModel objArgument) {
//        if (strArgument.isBlank() || objArgument != null) {
//            return new Response("Неверные аргументы команды", "");
//
//        } else {
//            try {
//                var collection = collectionService.averageOfDistance();
//                return new Response("Средняя дистанция: " + collectionService.averageOfDistance(), collection.toString());
//
//            } catch (IllegalArgumentException e) {
//                return new Response("Нет элементов в коллекции", "");
//            } catch (NonExistingElementException | DBProviderException e) {
//                return new Response(e.getMessage(), "");
//            }
//        }
//    }

    public Response averageOfDistance(User user, String strArgument, RouteModel objArgument) {
        if (!strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");
        } else {
            try {
                // Предполагается, что метод collectionService.averageOfDistance принимает пользователя
                double averageDistance = collectionService.averageOfDistance(user);
                return new Response("Средняя дистанция: " + averageDistance, "");
            } catch (NonExistingElementException e) {
                return new Response("Нет элементов в коллекции", "");
            } catch (Exception e) {
                return new Response("Произошла ошибка при вычислении средней дистанции", "");
            }
        }
    }


    public Response countByDistance(User user, String strArgument, RouteModel objArgument) {
        if (strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            try {
                int distance = Integer.parseInt(strArgument);
                if (distance > 0) {
                    var count = collectionService.countByDistance(distance);
                    return new Response("Количество маршрутов с дистанцией " + distance + ":   " + count, "");

                } else {
                    return new Response("Дистанция не может быть отрицательной", "");
                }

            } catch (NumberFormatException e) {
                return new Response("Неверный формат аргументов", "");
            }
        }
    }

    public Response filterGreaterThanDistance(User user, String strArgument, RouteModel objArgument) {
        if (strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            try {
                int distance = Integer.parseInt(strArgument);
                if (distance > 0) {
                    var collection = collectionService.filterGreaterThanDistance(distance);
                    return new Response("Коллекция отсортирована по дистанции", collection.toString());
                } else{
                    return new Response("Дистанция не может быть отрицательной", "");
                }
            } catch (NonExistingElementException e) {
                return new Response(e.getMessage(), "");
            }

        }
    }


    public static void addCommand(String command) {
        if (commandHistory.size() == 7) {
            commandHistory.removeFirst();
        }
        commandHistory.addLast(command);
    }
}