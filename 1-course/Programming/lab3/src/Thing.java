public abstract class Thing {
    protected String name;
    protected int id;
    protected int co = 0;

    public Thing(String b) {
        this.id = co;
        co += 1;
        this.name = b;
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
            Thing a = (Thing) obj;
            return this.id == a.id;
        }
    }



}
