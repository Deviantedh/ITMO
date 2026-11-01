public class Termometer extends Thing{
    private float temperature = -7;
    public Termometer(String b) {
        super(b);
    }
    public void changetemp() throws TermometerException{
        temperature = (int)(Math.random() * -8) + (-15);
        if (temperature <= -10) {
            System.out.println("На внутреннем термометре Дэнни: " + temperature + " градусов");
        }
        else {
            throw new TermometerException("Температура недостаточно низко опустилась");
        }
    }


}
