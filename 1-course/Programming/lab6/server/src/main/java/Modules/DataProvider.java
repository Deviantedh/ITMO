package Modules;

import CollectionObject.Route;

import java.util.Stack;

public interface DataProvider {

    void save(Stack<Route> collection);

    void load();
}
