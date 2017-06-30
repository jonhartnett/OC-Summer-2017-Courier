package edu.oc.courier.data;

import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

@Entity
public class Node {
    private static class RoutingEntry{
        Node next;
        double cost;

        RoutingEntry(Node next, double cost){
            this.next = next;
            this.cost = cost;
        }

        @Override
        public String toString() {
            return next + "," + cost;
        }
    }
    private static class RouteException extends RuntimeException{}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @ElementCollection
    public Map<Node, Double> inverseLinks = new HashMap<>();

    @Transient
    HashMap<Node, RoutingEntry> routingTable = new HashMap<>();
    @Transient
    private boolean dirty = false;

    public Node(){}

    public Node(String name){
        this.name = name;
        initRoutingTable();
    }

    void initRoutingTable(){
        routingTable.put(this, new RoutingEntry(this, 0));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        final Node node = (Node)o;
        return name != null && name.equals(node.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public Route getRoute(Node dest){
        try{
            System.out.println("New Route");
            Stream.Builder builder = Stream.builder();
            double cost = this.constructRoute(this, dest, builder);
            return new Route(builder.build(), cost);
        }catch(RouteException ex){
            return null;
        }
    }

    private double constructRoute(Node prev, Node dest, Stream.Builder builder){
        builder.add(this.name);
        if(dest == this)
            return inverseLinks.getOrDefault(prev, 0.0);

        RoutingEntry entry = routingTable.get(dest);
        if(entry == null)
            throw new RouteException();
        Node next = entry.next;
        return inverseLinks.getOrDefault(prev, 0.0) + next.constructRoute(this, dest, builder);
    }

    public void link(Node next, double cost){
        if(!Double.isInfinite(cost)){
            next.inverseLinks.put(this, cost);
        }else{
            Double prev = next.inverseLinks.remove(this);
            if(prev == null)
                return;
        }

        Set<Entry<Node, RoutingEntry>> entries = new HashSet<>(next.routingTable.entrySet());
        for(Entry<Node, RoutingEntry> entry : entries){
            this.update(entry.getKey(), next, entry.getValue().cost + cost);
        }
    }
    public void unlink(Node next){
        this.link(next, Double.POSITIVE_INFINITY);
    }

    public void revive(){
        propagate(this, 0);
    }

    private void update(Node dest, Node next, double cost){
        if(this.dirty){
            this.dirty = false;
            this.dirtyUpdate(dest);
            return;
        }
        RoutingEntry entry = routingTable.get(dest);
        if(entry != null){
            if(cost < entry.cost){
                entry.next = next;
                entry.cost = cost;
                this.propagate(dest, entry.cost);
            }else
            if(entry.next == next && cost > entry.cost){
                this.purge(dest, next);
                this.update(dest, next, cost);
            }
        }else
        if(!Double.isInfinite(cost)){
            entry = new RoutingEntry(next, cost);
            routingTable.put(dest, entry);
            this.propagate(dest, entry.cost);
        }
    }
    private void dirtyUpdate(Node dest){
        RoutingEntry entry = this.getBestEntry(dest);
        if(entry != null){
            routingTable.put(dest, entry);
            this.propagate(dest, entry.cost);
        }else{
            routingTable.remove(dest);
        }
    }
    private void purge(Node dest, Node next){
        RoutingEntry entry = routingTable.get(dest);
        if (entry != null && entry.next == next) {
            this.dirty = true;
            routingTable.remove(dest);
            for(Entry<Node, Double> link : inverseLinks.entrySet())
                link.getKey().purge(dest, this);
        }
    }
    private void propagate(Node dest, double cost){
        for(Entry<Node, Double> link : inverseLinks.entrySet()){
            Node node = link.getKey();
            double linkCost = link.getValue();
            node.update(dest, this, cost + linkCost);
        }
    }
    private RoutingEntry getBestEntry(Node dest){
        RoutingEntry entry = new RoutingEntry(null, Double.POSITIVE_INFINITY);
        for(Entry<Node, Double> link : inverseLinks.entrySet()){
            Node node = link.getKey();
            double linkCost = link.getValue();
            RoutingEntry linkEntry = node.routingTable.get(dest);
            if(linkEntry != null){
                if(linkEntry.cost + linkCost < entry.cost){
                    entry.next = node;
                    entry.cost = linkEntry.cost + linkCost;
                }
            }
        }
        if(Double.isInfinite(entry.cost))
            return null;
        else
            return entry;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .toString();
    }
}
