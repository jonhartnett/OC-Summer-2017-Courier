package edu.oc.courier.data;

import edu.oc.courier.Triple;

import java.util.*;

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

    public void setLink(final String src, final String dest, final RouteCondition cost){
        final Node srcNode = get(src);
        final Node destNode = get(dest);
        srcNode.link(destNode, cost);
        destNode.link(srcNode, cost);
    }
    public void setLink(final Node src, final Node dest, final RouteCondition cost){
        setLink(src.getName(), dest.getName(), cost);
    }
    public void setOneWayLink(final String src, final String dest, final RouteCondition cost){
        final Node srcNode = get(src);
        final Node destNode = get(dest);
        srcNode.link(destNode, cost);
        destNode.link(srcNode, null);
    }
    public void setOneWayLink(final Node src, final Node dest, final RouteCondition cost){
        setOneWayLink(src.getName(), dest.getName(), cost);
    }
    public void removeLink(final String key, final String key2){
        this.setLink(key, key2, null);
    }
    public void removeLink(final Node node, final Node node2){
        this.setLink(node, node2, null);
    }

    public boolean hasLink(final String key, final String key2){
        return getLink(key, key2) != null;
    }
    public boolean hasLink(final Node node, final Node node2){
        return getLink(node, node2) != null;
    }
    public RouteCondition getLink(final String key, final String key2){
        return get(key2).inverseLinks.getOrDefault(get(key), null);
    }
    public RouteCondition getLink(final Node node, final Node node2){
        return getLink(node.getName(), node2.getName());
    }

    public Collection<Triple<Node, Node, RouteCondition>> links(){
        List<Triple<Node, Node, RouteCondition>> list = new ArrayList<>();
        for(Node node : values()){
            for(Map.Entry<Node, RouteCondition> entry : node.inverseLinks.entrySet()){
                if(node.getId() < entry.getKey().getId() || !entry.getKey().inverseLinks.containsKey(node)) //avoid duplicates
                    list.add(new Triple<>(node, entry.getKey(), entry.getValue()));
            }
        }
        return list;
    }

    public Route getRoute(final String start, final String end){
        return get(start).getRoute(get(end));
    }
    public Route getRoute(final Node start, final Node end){
        return getRoute(start.getName(), end.getName());
    }
}
