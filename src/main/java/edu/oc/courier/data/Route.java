package edu.oc.courier.data;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Route {
    public Stream<String> path;
    public double cost;

    protected Route(Stream<String> path, double cost){
        this.path = path;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return cost + " " + path.map(Object::toString).collect(Collectors.joining("->"));
    }
}
