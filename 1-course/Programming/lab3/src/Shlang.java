import Inter.Lookslike;

public class Shlang extends Thing {
    private String name;
    private int id;
    private static int co = 0;

    public Shlang(String b) {
        super(b);
        this.id = co;
        co += 1;
        this.name = b;
    }
    public String getName() {
        return this.name;
    }
    public Lookslike looks = new Lookslike() {
        @Override
        public void Looks(String a) {
            System.out.println(Shlang.this.name + " " + a);
        }
    };
}


