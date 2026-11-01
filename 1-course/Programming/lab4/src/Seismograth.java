import Actors.Device;
import Conditions.Device_type;
import Ex.UnnormiusValueException;

import java.util.Random;

public class Seismograth extends Device {
    private class Record{
        public int G;
        public Record(){//вложенный
            Random random = new Random();
            int randomNumber = random.nextInt(100) + 20;
            G = randomNumber;
    }}
    public Seismograth(String name, int id, Device_type type) {
        super(name, id, type);
    }
    @Override
    protected void dowork() throws UnnormiusValueException {
        System.out.println(this.name + " регистрирует сейсмическую активность");
        Record record = new Record();
        if (record.G >= 95){
            throw new UnnormiusValueException("Сегодня на улице землетрясение...");
        }
    }
}