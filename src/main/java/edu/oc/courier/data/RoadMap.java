package edu.oc.courier.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RoadMap {
    public static RoadMap get(){
        final RoadMap map = new RoadMap();
        map.load();
        return map;
    }

    private Map<String, Node> nodes = new HashMap<>();

    public RoadMap(){}

    public RoadMap(final Iterable<Node> nodes){
        for(Node node : nodes)
            this.nodes.put(node.getName(), node);
    }

    public void save(){
        for(Node node : nodes.values())
            Node.table.set(node);
    }

    public void load(){
        this.clear();
        Node.table.getAll().forEach(node -> {
            node.initRoutingTable();
            nodes.put(node.getName(), node);
            node.revive();
        });
    }

    public boolean has(String key) {
        return nodes.containsKey(key);
    }

    public int size() { return nodes.size(); }
    public boolean isEmpty() { return nodes.isEmpty(); }
    public boolean containsKey(String key) { return nodes.containsKey(key); }
    public Node get(final String key) { return nodes.get(key); }
    public Node add(final String key) {
        if(nodes.containsKey(key))
            throw new RuntimeException("Name " + key + " already in use.");
        final Node node = new Node(key);
        nodes.put(key, node);
        return node;
    }
    public Node remove(final String key) {
        return nodes.remove(key);
    }
    public void clear(){
        nodes.clear();
    }
    public Set<String> keySet() { return nodes.keySet(); }
    public Collection<Node> values() { return nodes.values(); }

    public void setLink(final String key, final String key2, final RouteCondition cost){
        final Node n1 = nodes.get(key);
        final Node n2 = nodes.get(key2);
        n1.link(n2, cost);
        n2.link(n1, cost);
    }
    public void setOneWayLink(final String src, final String dest, final RouteCondition cost){
        nodes.get(src).link(nodes.get(dest), cost);
    }
    public void removeLink(final String key, final String key2){
        this.setLink(key, key2, null);
    }

    public RouteCondition getLink(final String key, final String key2){
        final Node node = get(key);
        final Node node2 = get(key2);
        return node2.inverseLinks.getOrDefault(node, null);
    }

    public Route getRoute(final String start, final String end){
        return getRoute(nodes.get(start), nodes.get(end));
    }
    public Route getRoute(final Node start, final Node end){
        return start.getRoute(end);
    }
}
