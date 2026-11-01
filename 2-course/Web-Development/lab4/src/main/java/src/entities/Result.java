package src.entities;

import javax.persistence.*;

@Entity
@Table(name = "results")
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "x_cord", nullable = false)
    private double xCord;
    @Column(name = "y_cord", nullable = false)
    private double yCord;
    @Column(name = "radius", nullable = false)
    private double radius;
    @Column(name = "result", nullable = false)
    private boolean result;
    @Column(name = "requesttime", nullable = false)
    String requestTime;
    @Column(name = "executiontime", nullable = false)
    private long executionTime;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Result(){}

    public Result(int id, double xCord, double yCord, double radius, boolean resuxlt, String requestTime, long executionTime, User user) {
        this.id = id;
        this.xCord = xCord;
        this.yCord = yCord;
        this.radius = radius;
        this.result = result;
        this.requestTime = requestTime;
        this.executionTime = executionTime;
        this.user = user;
    }

    @Override
    public String toString() {
        double temp_y = (double)((int)(yCord*100))/100.0;
        double temp_x = (double)((int)(xCord*100))/100.0;
        return temp_x+"%"+temp_y+"%"+this.radius+"%"+this.requestTime+"%"+this.executionTime+"%"+(this.result ? "true" : "false");
    }


    public int getId() {
        return id;
    }

    public double getxCord() {
        return xCord;
    }

    public double getyCord() {
        return yCord;
    }

    public double getRadius() {
        return radius;
    }

    public boolean getResult() {
        return result;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public User getUser() {
        return user;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setxCord(double xCord) {
        this.xCord = xCord;
    }

    public void setyCord(double yCord) {
        this.yCord = yCord;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
