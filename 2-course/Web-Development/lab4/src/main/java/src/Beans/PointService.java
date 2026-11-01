package src.Beans;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import src.DAOServices.ResultDao;
import src.DAOServices.UserDAO;
import src.Utils.DateFormer;
import src.entities.Result;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class PointService {
//    UserDAO userDAO = new UserDAO();
//    ResultDao resultDao = new ResultDao();
//    private final DateFormer dateFormer= new DateFormer();
    @Inject
    private UserDAO userDAO;

    @Inject
    private ResultDao resultDao;

    @Inject
    private DateFormer dateFormer;


    @Transactional
    public void clearPointHistory(String userID){
        int id =0;
        try{
            id = Integer.parseInt(userID);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        resultDao.deleteResultsByUserId(id);
    }
    @Transactional
    public void checkPoint(String x_cord, String y_cord, String radius, String userID) {
        double x = 0;
        double y = 0;
        double r = 0;
        int id = -1;
        try{
            x = Double.parseDouble(x_cord);
            y = Double.parseDouble(y_cord);
            r = Double.parseDouble(radius);
            id = Integer.parseInt(userID);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        long start_time = System.nanoTime();
        boolean result = pointBelongs(x, y, r);
        long execution_time = System.nanoTime() - start_time;
        Result res = new Result();
        res.setxCord(x);
        res.setyCord(y);
        res.setRadius(r);
        res.setResult(result);
        res.setRequestTime(dateFormer.getCurrentDate());
        res.setExecutionTime(execution_time);
        res.setUser(userDAO.getUserById(id));
        resultDao.saveResult(res);
    }

    public String getUserHistory(String userID){
        int id = -1;
        try{
            id = Integer.parseInt(userID);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        List<Result> resultHistory = resultDao.getResultsByUserId(id);
        ArrayList<String> ret = new ArrayList<>();
        for (Result result : resultHistory) {
            ret.add(result.toString());
        }
        return String.join("\n", ret);
    }

    private boolean pointBelongs(double x, double y, double r) {
        boolean firstFig=(y>=0 && y<=r) && (x>=0 && x<=r);
        boolean secondFig=((x*x+y*y)<=(r*r/4)) && (x<=0) && (y>=0);
        boolean thirdFig = (x >= -2 * (y + r / 2)) && (x <= 0) && (y <= 0);
        return (firstFig || secondFig ||thirdFig);
    }
}
