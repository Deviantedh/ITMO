import Actors.Device;
import Conditions.Device_type;

import java.lang.reflect.Type;

public class Building extends Device {

    public Building(String name, int did, Device_type Type) {
        super(name, did, Type);
    }

    @Override
    protected void dowork() {
        System.out.println(this.name + " stayng");
    }
}
