import Actors.Device;
import Conditions.Device_type;
import Ex.UnnormiusValueException;

import java.util.Random;

public class RainGauge extends Device{
    private static class Bowl{ //статический вложенный
        public int V;
        public Bowl(){
            Random random = new Random();
            int randomNumber = random.nextInt(100) + 20;
            V = randomNumber;
        }
    }
    public RainGauge(String name, int id, Device_type type) {
        super(name, id, type);
    }
    @Override
    protected void dowork () throws UnnormiusValueException {
        System.out.println(this.name + " регистрирует уровень осадков");
        Bowl bowl = new Bowl();
        if (bowl.V >=90){
            throw new UnnormiusValueException("Сегодня на улице непогода...");
        }
    }

}
