package com.devi.main.mbeans;

public class MissPercentage implements MissPercentageMBean {
    private PointsCounterMBean pointsCounter;

    public MissPercentage(PointsCounterMBean pointsCounter) {
        this.pointsCounter = pointsCounter;
    }

    @Override
    public double getMissPercentage() {
        int totalPoints = pointsCounter.getTotalPoints();
        int missedPoints = pointsCounter.getMissedPoints();
        if (totalPoints == 0) {
            return 0.0;
        }
        return (double) missedPoints / totalPoints * 100;
    }
    @Override
    public double getHitPercentage() {
        // просто дополняем до 100%
        return 100.0 - getMissPercentage();
    }
}