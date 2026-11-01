package com.devi.main.mbeans;

public interface HitRatioMBean {
    /** % попаданий от общего числа кликов */
    double getHitPercentage();
    /** % промахов от общего числа кликов */
    double getMissPercentage();
}