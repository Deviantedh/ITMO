package Utils;

import CollectionObject.Coordinates;
import CollectionObject.RouteModel;
import CollectionObject.Location;
import Exceptions.EmptyFieldException;
import Exceptions.NegativeFieldException;
import Network.User;

import java.util.Scanner;

public class RouteAsker {

    public static RouteModel createElement(User user){
        Scanner InputScanner = PromptScan.getUserScanner();

        System.out.println("Введите имя");
        String name = askString(InputScanner);

        System.out.println("Введите координату x:");
        float x = askX(InputScanner);

        System.out.println("Введите координату y:");
        float y = askY(InputScanner);

        Coordinates coordinates = new Coordinates(x, y);

        System.out.println("Заполните откуда маршрут:");
        Location from = askLocation(InputScanner);

        System.out.println("Заполните куда маршрут:");
        Location to = askLocation(InputScanner);

        System.out.println("Введите дистанцию");
        int distance = askInt(InputScanner);

        return new RouteModel(name, coordinates, from, to, distance, user);
    }

    private static String askString(Scanner InputScanner) {
        while(true) {
            try {
                var name = InputScanner.nextLine();
                if (name.isBlank()){
                    throw new EmptyFieldException("Поле не может быть пустым. Введите его ещё раз: ");
                }
                return name.trim();
            }catch(EmptyFieldException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static float askX(Scanner InputScanner) {
        while(true) {
            try {
                return Float.parseFloat(InputScanner.nextLine());
            }catch (NumberFormatException e){
                System.out.println("Неверный формат числа. Введите его повторно:");
            }
        }
    }

    private static float askY(Scanner InputScanner) {
        while(true) {
            try {
                return Float.parseFloat(InputScanner.nextLine());
            }catch (NumberFormatException e){
                System.out.println("Неверный формат числа. Введите его повторно:");
            }
        }
    }

    private static float askFloat(Scanner InputScanner) {
        while(true) {
            try {
                float num = Float.parseFloat(InputScanner.nextLine());
                if (num > 0){
                    return num;
                } else {
                    throw new NegativeFieldException("Число не может быть отрицательным. Введите его ещё раз:");
                }

            } catch (NumberFormatException e){
                System.out.println("Неверный формат числа. Введите его повторно:");
            } catch (NegativeFieldException e){
                System.out.println(e.getMessage());
            }
        }
    }

    private static double askDouble(Scanner InputScanner) {
        while(true) {
            try {
                double num = Double.parseDouble(InputScanner.nextLine());
                if (num > 0){
                    return num;
                } else {
                    throw new NegativeFieldException("Число не может быть отрицательным. Введите его ещё раз:");
                }
            } catch (NumberFormatException e){
                System.out.println("Неверный формат числа. Введите его повторно:");
            } catch (NegativeFieldException e){
                System.out.println(e.getMessage());
            }
        }
    }

    private static int askInt(Scanner InputScanner) {
        while(true) {
            try {
                int num = Integer.parseInt(InputScanner.nextLine());
                if (num > 0){
                    return num;
                } else {
                    throw new NegativeFieldException("Число не может быть отрицательным. Введите его ещё раз:");
                }
            } catch (NumberFormatException e){
                System.out.println("Неверный формат числа. Введите его повторно:");
            } catch (NegativeFieldException e){
                System.out.println(e.getMessage());
            }
        }
    }

    private static long askLong(Scanner InputScanner) {
        while(true) {
            try {
                long num = Long.parseLong(InputScanner.nextLine());
                if (num > 0){
                    return num;
                } else {
                    throw new NegativeFieldException("Число не может быть отрицательным. Введите его ещё раз:");
                }
            } catch (NumberFormatException e){
                System.out.println("Неверный формат числа. Введите его повторно:");
            } catch (NegativeFieldException e){
                System.out.println(e.getMessage());
            }
        }
    }

    private static Location askLocation(Scanner InputScanner) {
        while (true){
            System.out.println("Введите координату x:");
            float lX = askFloat(InputScanner);

            System.out.println("Введите координату y:");
            int lY = askInt(InputScanner);

            System.out.println("Введите координату z:");
            float lZ = askFloat(InputScanner);

            System.out.println("Введите название места:");
            String lName = askString(InputScanner);

            Location location = new Location(lX, lY, lZ, lName);
            return location;
        }
    }
}
