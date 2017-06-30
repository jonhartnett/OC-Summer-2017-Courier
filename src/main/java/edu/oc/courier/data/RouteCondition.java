package edu.oc.courier.data;

public enum RouteCondition {
    OPEN(1), BUSY(2), CLOSED(Integer.MAX_VALUE), REMOVE(-1);

    private final int value;

    RouteCondition(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
