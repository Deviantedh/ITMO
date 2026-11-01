package Actors;
import Actors.*;
import Conditions.Device_type;
import Moves.*;

public class Policeman extends Human implements Policeman_moves, Policeman_attacks{

    public Policeman(String name, int id) {
        super(name, id);
    }

    @Override
    public void shoot(String a)
    {
         class Weapon{ //локальный
            public String name = "Автомат";
        }
        Weapon weapon = new Weapon();
        System.out.println(this.name + " стреляли" + " из " + weapon.name + a);
    }

    @Override
    public void broke(String a, Device obj, int id ){
            if (obj.hashCode() == id) {
                obj.change_hp(false);
                System.out.println(this.name + " " + a + "" + obj.name);
            } else {
                System.out.println("Объект с указанным ID не найден.");
            }
        }
        //System.out.println(this.name + " " + a);


    @Override
    public void run(String a) {
        System.out.println(this.name + " побежали" + a);
    }

    @Override
    public void fly(String a) {
        System.out.println(this.name + " " + a);
    }

    @Override
    public void climb(Device obj, int id) throws RuntimeException {
        if (obj.Type== Device_type.Building) {
            if (obj.hashCode() == id) {
                obj.change_hp(false);
                System.out.println(this.name + " полезли на " + obj.name);
            } else {
                System.out.println("Объект с указанным ID не найден.");
            }
        } else {
            System.out.println("На не билдинги забираться нельзя!");
            //throw new RuntimeException("На не билдинги забираться нельзя!");
        }
        //System.out.println(this.name + " полезли" + a);
    }

}
