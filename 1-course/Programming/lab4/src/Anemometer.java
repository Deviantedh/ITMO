import Actors.Device;
import Conditions.Device_type;

public class Anemometer extends Device {

    public Anemometer(String name, int id, Device_type type) {
        super(name, id, type);
    }

    @Override
    protected void dowork() {
        System.out.println(this.name + " регистрирует скорость ветра.");
    }
}