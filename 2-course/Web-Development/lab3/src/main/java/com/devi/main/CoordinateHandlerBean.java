package com.devi.main;

import com.devi.main.utils.AreaChecker;
import com.devi.main.mbeans.PointsCounterMBean;
import jakarta.inject.Inject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class CoordinateHandlerBean implements Serializable {
    private double x;
    private double y;
    private double r;
    @Inject
    private PointsCounterMBean pointsCounter;

    public void onUserClick(double x, double y) {
        // используем именно isInArea и передаём r из поля бина
        boolean inArea = AreaChecker.isInArea(x, y, this.r);

        // уведомляем наш MBean
        pointsCounter.pointAdded(inArea, x, y);
    }
}