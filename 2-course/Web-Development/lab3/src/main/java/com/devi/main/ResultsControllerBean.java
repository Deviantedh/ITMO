package com.devi.main;

import com.devi.main.db.DAOFactory;
import com.devi.main.entity.ResultEntity;
import com.devi.main.mbeans.MissPercentage;
import com.devi.main.mbeans.MissPercentageMBean;
import com.devi.main.mbeans.PointsCounter;
import com.devi.main.mbeans.PointsCounterMBean;
import com.devi.main.utils.AreaChecker;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.management.MBeanServer;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Locale;

@Getter
@Setter
@Slf4j
public class ResultsControllerBean implements Serializable {
    private XCoordinateBean xCoordinateBean;
    private YCoordinateBean yCoordinateBean;
    private RCoordinateBean rCoordinateBean;
    private PointsCounterMBean pointsCounter;
    private MissPercentageMBean missPercentage;

    private ArrayList<ResultEntity> results = new ArrayList<>();

    /**
     * Initializes the bean and loads existing results from the database.
     */
    @PostConstruct
    public void init() {
        var resultsEntities = DAOFactory.getInstance().getResultDAO().getAllResults();
        results = new ArrayList<>(resultsEntities);
        log.info("Results initialized with {} entries.", results.size());

        // Регистрация MBeans
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName pointsCounterName = new ObjectName("com.devi.main.mbeans:type=PointsCounter");
            pointsCounter = new PointsCounter(DAOFactory.getInstance().getResultDAO());
            mbs.registerMBean(pointsCounter, pointsCounterName);

            // Регистрация слушателя уведомлений
            NotificationListener listener = (notification, handback) -> System.out.println("Received notification: " + notification.getMessage());
            mbs.addNotificationListener(pointsCounterName, listener, null, null);

            missPercentage = new MissPercentage(pointsCounter);
            ObjectName missPercentageName = new ObjectName("com.devi.main.mbeans:type=MissPercentage");
            StandardMBean missPercentageMBean = new StandardMBean(missPercentage, MissPercentageMBean.class);
            mbs.registerMBean(missPercentageMBean, missPercentageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addResult(Double x, Double y, Double r) {
        boolean result = AreaChecker.isInArea(x, y, r);
        if (result) {
            pointsCounter.incrementTotalPoints();
            pointsCounter.resetConsecutiveMisses();
        } else {
            pointsCounter.incrementTotalPoints();
            pointsCounter.incrementMissedPoints();
        }

        ResultEntity en = ResultEntity.builder().x(x).y(y).r(r).result(result).build();

        results.add(en);
        DAOFactory.getInstance().getResultDAO().addNewResult(en);
        log.info("Added new result to the db: X={}, Y={}, R={}", x, y, r);

        String script = String.format(
                Locale.US, "window.drawDotOnCanvas(%f, %f, %f, %b, true);", x, y, r,
                result);
        FacesContext.getCurrentInstance()
                .getPartialViewContext()
                .getEvalScripts()
                .add(script);
    }

    public void updateCanvas(double r) {
        for (ResultEntity en : results) {
            boolean result = AreaChecker.isInArea(en.getX(), en.getY(), r);
            en.setR(r);
            en.setResult(result);

            String script = String.format(
                    Locale.US, "window.drawDotOnCanvas(%f, %f, %f, %b, true);",
                    en.getX(), en.getY(), r, result);
            System.out.println("Script: " + script);
            FacesContext.getCurrentInstance()
                    .getPartialViewContext()
                    .getEvalScripts()
                    .add(script);
        }
        log.info("Canvas updated with new radius: {}", r);
    }

    /**
     * Clears the database and locally stored array from all the points
     */
    public void clearResults() {
        DAOFactory.getInstance().getResultDAO().clearResults();
        results.clear();
        pointsCounter.resetAndInitializeCounts();
        log.info("All results cleared from database and local array.");
    }
}
