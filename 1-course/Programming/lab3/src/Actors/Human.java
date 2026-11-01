package Actors;

import Inter.Action;
import Inter.Feelings;
import Inter.Move;
import Inter.Think;

public class Human extends Creature  {

    private String name;
    private int id;
    private static int co = 0;

    public Human(String b) {
        this.id = co;
        co += 1;
        this.name = b;
    }



    public Think decide = new Think() {
        @Override
        public void doing(String a, String b) {
            System.out.println(b + Human.this.name + " решил " + a);
        }
    };

    public Think think = new Think() {
        @Override
        public void doing(String a, String b) {
            System.out.println(b + Human.this.name + " подумал " + a);
        }
    };
    public Feelings feels = new Feelings() {
        @Override
        public void feel(String a) {
            System.out.println(Human.this.name + " почувствовал, что " + a);
        }
    };
    public Action action = new Action() {
        @Override
        public void action(String a){
            System.out.println(Human.this.name + " " + a);
        }
    };
    public Move moves = new Move<>(){

        @Override
        public void shag() {
            System.out.println(Human.this.name + " сделал шаг.");
        }

        @Override
        public void run() {
            System.out.println(Human.this.name + " побежал.");
        }
    };



    public String getName() {
        return this.name;
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

        @Override
        public void saw(String a) {
            System.out.println(Human.this.name + " увидел " + a);
        }

        @Override
        public void heard(String a) {
            System.out.println(Human.this.name + "услышал " + a);
        }
    }

