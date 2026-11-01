import Actors.Device;
import Conditions.Device_type;
import Ex.UnnormiusValueException;

import java.util.Random;

public class Barometer extends Device {
    private int P = 120;

    public Barometer(String name, int id, Device_type type) {
        super(name, id, type);
    }
    @Override
    protected void dowork() throws UnnormiusValueException {
        System.out.println(this.name + " регистрирует уровень давления");
        Random random = new Random();
        int randomNumber = random.nextInt(193) + 20;
        int P = randomNumber;
        if (40 <= P && P <= 180) {
            System.out.println("Атмосферное давление на улице составляло: " + P + " паскалей.");
        }
        else {
            throw new UnnormiusValueException("Давление не совместимое с жизнью.");
        }
    }
}