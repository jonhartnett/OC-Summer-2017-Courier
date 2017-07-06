package edu.oc.courier.data;

import java.util.ArrayList;
import java.util.List;

public class Route {
    public final List<String> path;
    public final double cost;

    protected Route(final List<String> path, final double cost){
        this.path = path;
        this.cost = cost;
    }

    private enum Direction{
        NORTH, SOUTH, EAST, WEST;
    }

    @Override
    public String toString() {
        List<String[]> nodes = new ArrayList<>();
        for(String node : path)
            nodes.add(node.split(" and "));
        StringBuilder result = new StringBuilder("Leave travelling ");
        Direction dir;
        if(nodes.get(0)[1] == nodes.get(1)[1]){
            dir = nodes.get(0)[0].compareTo(nodes.get(1)[0]) > 0 ? Direction.WEST : Direction.EAST;
            if(nodes.get(0)[1].compareTo(nodes.get(1)[1]) > 0)
                result.append("south");
            else
                result.append("north");
            result.append(" on ");
            result.append(nodes.get(0)[0]);
        }else{
            dir = nodes.get(0)[1].compareTo(nodes.get(1)[1]) > 0 ? Direction.NORTH : Direction.SOUTH;
            if(nodes.get(0)[0].compareTo(nodes.get(1)[0]) > 0)
                result.append("west");
            else
                result.append("east");
            result.append(" on ");
            result.append(nodes.get(0)[1]);
        }
        result.append(". ");
        String[] prev = nodes.get(0);
        int count = 0;
        for(String[] node : nodes){
            switch(dir){
                case NORTH:
                case SOUTH:{
                    boolean left = (prev[0].compareTo(node[0]) > 0) == (dir == Direction.SOUTH);
                    result.append(" Travel ");
                    result.append(count);
                    result.append(" blocks and turn ");
                    result.append(left ? "left" : "right");
                    result.append(".");
                }
                case EAST:
                case WEST:{
                    boolean left = (prev[1].compareTo(node[1]) > 0) == (dir == Direction.EAST);
                    result.append(" Travel ");
                    result.append(count);
                    result.append(" blocks and turn ");
                    result.append(left ? "left" : "right");
                    result.append(".");
                }
            }
            prev = node;
            count++;
        }
        return result.toString();
    }
}
