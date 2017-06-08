package edu.oc.courier;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Route<K> {
    public Stream<K> path;
    public double cost;

    protected Route(Stream<K> path, double cost){
        this.path = path;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return cost + " " + path.map(Object::toString).collect(Collectors.joining("->"));
    }
}
