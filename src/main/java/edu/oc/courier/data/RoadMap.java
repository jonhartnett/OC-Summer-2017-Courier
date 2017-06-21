package edu.oc.courier.data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class RoadMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Node> nodeList = new ArrayList<>();
    @Transient
    private Map<String, Node> nodes = new HashMap<>();

    public RoadMap(){}

    public RoadMap(Iterable<Node> nodes){
        for(Node node : nodes)
            this.nodes.put(node.getName(), node);
    }

    public void onLoad(){
        for(Node node : nodeList){
            node.initRoutingTable();
            nodes.put(node.getName(), node);
            node.revive();
        }
    }

    public void add(String key){
        if(nodes.containsKey(key))
            throw new RuntimeException("Name " + key + " already in use.");
        Node node = new Node(key);
        nodeList.add(node);
        nodes.put(key, node);
    }

    public void clear(){
        nodeList.clear();
        nodes.clear();
    }

    public void setLink(String key, String key2, double cost){
        Node n1 = nodes.get(key);
        Node n2 = nodes.get(key2);
        n1.link(n2, cost);
        n2.link(n1, cost);
    }

    public void setOneWayLink(String src, String dest, double cost){
        nodes.get(src).link(nodes.get(dest), cost);
    }

    public void removeLink(String key, String key2){
        this.setLink(key, key2, Double.POSITIVE_INFINITY);
    }

    public Route getRoute(String start, String end){
        return nodes.get(start).getRoute(nodes.get(end));
    }
}
