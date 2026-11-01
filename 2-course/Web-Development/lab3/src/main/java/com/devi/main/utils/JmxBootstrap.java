package com.devi.main.utils;

import com.devi.main.db.DAOFactory;
import com.devi.main.db.ResultDAO;
import com.devi.main.mbeans.MissPercentage;
import com.devi.main.mbeans.PointsCounter;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class JmxBootstrap implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();

            ResultDAO dao = DAOFactory.getResultDAO();

            // 1) PointsCounter
            ObjectName counterName = new ObjectName("com.devi.main.mbeans:type=PointsCounter");
            PointsCounter counter = new PointsCounter(dao);
            server.registerMBean(counter, counterName);


            ObjectName percName = new ObjectName("com.devi.main.mbeans:type=MissPercentage");
            MissPercentage perc = new MissPercentage(counter);
            server.registerMBean(perc, percName);

        } catch (Exception ex) {
            throw new RuntimeException("Ошибка регистрации JMX MBean’ов", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}