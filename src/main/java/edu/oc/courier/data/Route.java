package edu.oc.courier.data;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Route {
    public final Stream<String> path;
    public final double cost;

    protected Route(final Stream<String> path, final double cost){
        this.path = path;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return cost + " " + path.map(Object::toString).collect(Collectors.joining("->"));
    }
}
