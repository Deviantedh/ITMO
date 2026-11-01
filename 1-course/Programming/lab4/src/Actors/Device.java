package Actors;

import Conditions.Device_type;
import Ex.UnnormiusValueException;

public abstract class Device {
    protected abstract void dowork() throws UnnormiusValueException;
    public String name;
    public boolean hp = true;
    public boolean condition = false;
    protected int id;
    public Device_type Type = Device_type.Null;
    protected int co;

    public Device(String name, int did, Device_type Type) {
        this.Type = Type;
        this.id = did;
        co += 1;
        this.name = name;
    }
    public void setType(Device_type Newtype){
        this.Type = Newtype;
    }
    public Device_type getType(){
        return this.Type;
    }
    public void change_cond(boolean cond) {
        this.condition = cond;
    }
    public void change_hp(boolean hp){
        this.hp = hp;
    }
    public String getName() {
        return name;
    }
    public void tellstate(){
        if(condition == true){
            System.out.println("Прибор '" + this.name + "' включен.");

        }
        else {
            System.out.println("Прибор '" + this.name + "' выключен.");
        }
    }
    public void tellstatus(){
        if(hp == true){
        System.out.println("Целостность прибора '" + this.name + "' равна 100~%");

        }
        else {
            System.out.println("Целостность прибора '" + this.name + "' нарушена, прибор сломан.");
        }
    }
    @Override
    public int hashCode() {
        return id;
    };

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj.getClass() != this.getClass()) {
            return false;
        } else {
            Device a = (Device) obj;
            return this.id == a.id;
        }
    }
}