package edu.oc.courier.data;

public enum RouteCondition {
    OPEN, LIGHT_TRAFFIC, TRAFFIC, HEAVY_TRAFFIC, CLOSED;

    double cost(){
        switch(this){
            case OPEN:
                return 1;
            case LIGHT_TRAFFIC:
                return 1.5;
            case TRAFFIC:
                return 2;
            case HEAVY_TRAFFIC:
                return 4;
            case CLOSED:
                return Double.POSITIVE_INFINITY;
            default:
                throw new RuntimeException("Invalid RouteCondition " + this.toString());
        }
    }
}
