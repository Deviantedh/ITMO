package Actors;
import Ex.IllegalStateException;

public class Human {
    protected int id;
    protected int co;
    protected String name;

    public Human(String name, int id) {
        this.id = id;
        co += 1;
        this.name = name;

    }
    public String getName() {
        return name;
    }

    public void heard(String a){  System.out.println(this.name + " услышал" + a);}


    public void feel(String a) {
        System.out.println(this.name + " " + a);
    }


    public void lookat(String a) {
        System.out.println(this.name + " посмотрел" + a);
    }


    public void scream(String a) {
        System.out.println(this.name + " закричал" + a);
    }


    public void think(String a) {
        System.out.println(this.name + a);
    }


    public void switch_dev(String a, Device obj, int id, boolean b, Human objh) throws IllegalStateException {
        if (obj.condition==b){
            if (obj.hashCode() == id) {
                obj.change_cond(b);
                System.out.println(this.name + " " + a + " " + obj.name);
            } else {
                System.out.println("Объект с указанным ID не найден.");
            }}
        else {
            if(b){
                throw new IllegalStateException(" попытался ", objh, obj, b);
            }
            else {
                throw new IllegalStateException(" попытался ", objh, obj, b);
            }
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
            Human a = (Human) obj;
            return this.id == a.id;
        }
    }
}