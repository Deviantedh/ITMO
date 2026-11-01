package src.rest;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ApplicationPath;
import src.resources.AuthorizationController;
import src.resources.PointCheckingController;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/rest")
public class RestApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(AuthorizationController.class);
        classes.add(PointCheckingController.class);
        return classes;
    }
}