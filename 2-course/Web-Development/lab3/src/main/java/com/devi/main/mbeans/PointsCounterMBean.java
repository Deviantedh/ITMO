package com.devi.main.mbeans;

public interface PointsCounterMBean {
    int getTotalPoints();
    int getMissedPoints();
    void incrementTotalPoints();
    void incrementMissedPoints();
    void resetConsecutiveMisses();
    void resetAndInitializeCounts();
    void resetCounters();
    /**
     * Метод для вызова при каждом клике в вашем UI-бине.
     * @param inArea true, если точка внутри области
     * @param x координата X
     * @param y координата Y
     */
    void pointAdded(boolean inArea, double x, double y);
}