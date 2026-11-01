import Actors.Device;
import Conditions.Device_type;

public class AntiGravityDevice extends Device {
    
    public AntiGravityDevice(String name, int id, Device_type type) {
        super(name, id, type);
    }
    public void showing(){};
    // Метод, специфичный для прибора невесомости
    @Override
    protected void dowork() {
        AntiGravityDevice device = new AntiGravityDevice(this.name, this.id, this.Type){ //анонимный
            @Override
            public void showing(){
                System.out.println(this.name + " существует.");
            }
        };
        device.showing();
    }
}
