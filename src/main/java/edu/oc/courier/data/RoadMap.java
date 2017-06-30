package edu.oc.courier.data;

import edu.oc.courier.DBTransaction;
import edu.oc.courier.Triple;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Stream;

@Entity
public class RoadMap {
    public static RoadMap getMap(DBTransaction trans) {
        Optional<RoadMap> optional = trans.getAny(RoadMap.class);
        if (optional.isPresent()) {
            RoadMap instance = optional.get();
            instance.onLoad();
            return instance;
        } else {
            RoadMap instance = new RoadMap();
            trans.save(instance);
            return instance;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id = 1;
    @OneToMany(cascade = CascadeType.ALL)
    public List<Node> nodeList = new ArrayList<>();
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

    public boolean has(String key) {
        return nodes.containsKey(key);
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

    public Stream<Triple<String, String, Double>> getLinks() {
        Stream.Builder<Triple<String, String, Double>> builder = Stream.builder();
        for (Node node : nodes.values()) {
            for (Map.Entry<Node, Double> entry : node.inverseLinks.entrySet()) {
                if (node.getName().compareTo(entry.getKey().getName()) <= 0) {
                    builder.add(new Triple<>(node.getName(), entry.getKey().getName(), entry.getValue()));
                }
            }
        }
        return builder.build();
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
