package edu.oc.courier.data;

public enum RouteCondition {
    OPEN(1), LIGHT_TRAFFIC(1.5), TRAFFIC(2), HEAVY_TRAFFIC(4), CLOSED(Double.POSITIVE_INFINITY);

    public final double cost;

    RouteCondition(final double cost){
        this.cost = cost;
    }
}
