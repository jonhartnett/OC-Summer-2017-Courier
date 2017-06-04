package edu.oc.courier.data;

import com.google.common.base.MoreObjects;
import java.util.List;
import java.util.Objects;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

@SuppressWarnings("unused")
public final class RoadMap {

    private DirectedGraph<Intersection, DefaultEdge> intersectionGraph;

    public List<DirectionStep> generateDirection(Intersection start, Intersection end) {
        throw new UnsupportedOperationException("TODO");
    }

    public DirectedGraph<Intersection, DefaultEdge> getIntersectionGraph() {
        return intersectionGraph;
    }

    public void setIntersectionGraph(DirectedGraph<Intersection, DefaultEdge> intersectionGraph) {
        this.intersectionGraph = intersectionGraph;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RoadMap roadMap = (RoadMap) o;
        return Objects.equals(intersectionGraph, roadMap.intersectionGraph);
    }

    @Override
    public int hashCode() {
        return Objects.hash(intersectionGraph);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("intersectionGraph", intersectionGraph)
            .toString();
    }
}
