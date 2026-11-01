package com.devi.main.mbeans;

public class HitRatio implements HitRatioMBean {
    private final PointsCounterMBean counter;

    public HitRatio(PointsCounterMBean counter) {
        this.counter = counter;
    }

    @Override
    public double getHitPercentage() {
        int total = counter.getTotalPoints();
        if (total == 0) return 0.0;
        return ((total - counter.getMissedPoints()) * 100.0) / total;
    }

    @Override
    public double getMissPercentage() {
        int total = counter.getTotalPoints();
        if (total == 0) return 0.0;
        return (counter.getMissedPoints() * 100.0) / total;
    }
}