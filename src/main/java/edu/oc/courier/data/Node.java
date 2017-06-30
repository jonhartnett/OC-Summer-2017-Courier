package edu.oc.courier.data;

import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@Entity
public class Node {
    private static class RoutingEntry{
        Node next;
        double cost;

        RoutingEntry(final Node next, final double cost){
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
    @Column(unique = true)
    private String name;
    @ElementCollection
    public Map<Node, Double> inverseLinks = new HashMap<>();

    @Transient
    HashMap<Node, RoutingEntry> routingTable = new HashMap<>();
    @Transient
    private boolean dirty = false;

    public Node(){}

    public Node(final String name){
        this.name = name;
        initRoutingTable();
    }

    void initRoutingTable(){
        routingTable.put(this, new RoutingEntry(this, 0));
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(final String name){
        this.name = name;
    }

    @Override
    public boolean equals(final Object o) {
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

    public Route getRoute(final Node dest){
        try{
            System.out.println("New Route");
            final Stream.Builder<String> builder = Stream.builder();
            final double cost = this.constructRoute(this, dest, builder);
            return new Route(builder.build(), cost);
        }catch(RouteException ex){
            return null;
        }
    }

    private double constructRoute(final Node prev, final Node dest, final Stream.Builder<String> builder){
        builder.add(this.name);
        if(dest == this)
            return inverseLinks.getOrDefault(prev, 0.0);

        final RoutingEntry entry = routingTable.get(dest);
        if(entry == null)
            throw new RouteException();
        final Node next = entry.next;
        return inverseLinks.getOrDefault(prev, 0.0) + next.constructRoute(this, dest, builder);
    }

    public void link(final Node next, final double cost){
        if(!Double.isInfinite(cost)){
            next.inverseLinks.put(this, cost);
        }else{
            final Double prev = next.inverseLinks.remove(this);
            if(prev == null)
                return;
        }

        final Set<Entry<Node, RoutingEntry>> entries = new HashSet<>(next.routingTable.entrySet());
        for(Entry<Node, RoutingEntry> entry : entries){
            this.update(entry.getKey(), next, entry.getValue().cost + cost);
        }
    }
    public void unlink(final Node next){
        this.link(next, Double.POSITIVE_INFINITY);
    }

    public void revive(){
        propagate(this, 0);
    }

    private void update(final Node dest, final Node next, final double cost){
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
    private void dirtyUpdate(final Node dest){
        final RoutingEntry entry = this.getBestEntry(dest);
        if(entry != null){
            routingTable.put(dest, entry);
            this.propagate(dest, entry.cost);
        }else{
            routingTable.remove(dest);
        }
    }
    private void purge(final Node dest, final Node next){
        final RoutingEntry entry = routingTable.get(dest);
        if (entry != null && entry.next == next) {
            this.dirty = true;
            routingTable.remove(dest);
            for(Entry<Node, Double> link : inverseLinks.entrySet())
                link.getKey().purge(dest, this);
        }
    }
    private void propagate(final Node dest, final double cost){
        for(Entry<Node, Double> link : inverseLinks.entrySet()){
            final Node node = link.getKey();
            final double linkCost = link.getValue();
            node.update(dest, this, cost + linkCost);
        }
    }
    private RoutingEntry getBestEntry(final Node dest){
        final RoutingEntry entry = new RoutingEntry(null, Double.POSITIVE_INFINITY);
        for(Entry<Node, Double> link : inverseLinks.entrySet()){
            final Node node = link.getKey();
            final double linkCost = link.getValue();
            final RoutingEntry linkEntry = node.routingTable.get(dest);
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
