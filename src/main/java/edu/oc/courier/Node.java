package main.java.edu.oc.courier;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.*;

public class Node<K> {
    private static class RoutingEntry<K>{
        Node<K> next;
        double cost;

        RoutingEntry(Node<K> next, double cost){
            this.next = next;
            this.cost = cost;
        }
    }

    public K name;
    private HashMap<Node<K>, Double> links = new HashMap<>();
    private HashMap<Node<K>, RoutingEntry<K>> routingTable = new HashMap<>();
    private boolean dirty = false;

    public Node(K name){
        this.name = name;
        routingTable.put(this, new RoutingEntry<K>(this, 0));
    }

    public Route<K> getRoute(Node<K> dest){
        try{
            Stream.Builder<K> builder = Stream.builder();
            double cost = this.constructRoute(dest, builder);
            return new Route<K>(builder.build(), cost);
        }catch(RuntimeException ex){
            return null;
        }
    }

    private double constructRoute(Node<K> dest, Stream.Builder<K> builder){
        builder.add(this.name);
        if(dest == this)
            return 0;
        RoutingEntry<K> entry = routingTable.get(dest);
        if(entry == null)
            throw new RuntimeException("No valid route!");
        Node<K> next = entry.next;
        return links.get(next) + next.constructRoute(dest, builder);
    }

    public void link(Node<K> next, double cost){
        links.put(next, cost);

        for(Entry<Node<K>, RoutingEntry<K>> entry : next.routingTable.entrySet())
            this.update(entry.getKey(), next, entry.getValue().cost + cost);
    }
    public void unlink(Node<K> next){
        this.link(next, Double.POSITIVE_INFINITY);
    }

    private void update(Node<K> dest, Node<K> next, double cost){
        if(this.dirty){
            this.dirty = false;
            this.dirtyUpdate(dest);
            return;
        }
        RoutingEntry<K> entry = routingTable.get(dest);
        if(entry != null){
            if(cost < entry.cost){
                entry.next = next;
                entry.cost = cost;
                dirty = true;
            }else
            if(entry.next == next && cost > entry.cost){
                this.purge(dest, next);
                this.update(dest, next, cost);
            }
        }else
        if(!Double.isInfinite(cost)){
            entry = new RoutingEntry<K>(next, cost);
            routingTable.put(dest, entry);
            this.propagate(dest, entry.cost);
        }
    }
    private void dirtyUpdate(Node<K> dest){
        RoutingEntry<K> entry = this.getBestEntry(dest);
        if(entry != null){
            routingTable.put(dest, entry);
            this.propagate(dest, entry.cost);
        }else{
            routingTable.remove(dest);
        }
    }
    private void purge(Node<K> dest, Node<K> next){
        RoutingEntry<K> entry = routingTable.get(dest);
        if(entry.next == next){
            this.dirty = true;
            routingTable.remove(dest);
            for(Entry<Node<K>, Double> link : links.entrySet())
                link.getKey().purge(dest, this);
        }
    }
    private void propagate(Node<K> dest, double cost){
        for(Entry<Node<K>, Double> link : links.entrySet()){
            Node node = link.getKey();
            double linkCost = link.getValue();
            node.update(dest, this, cost + linkCost);
        }
    }
    private RoutingEntry<K> getBestEntry(Node<K> dest){
        RoutingEntry<K> entry = new RoutingEntry<K>(null, Double.POSITIVE_INFINITY);
        for(Entry<Node<K>, Double> link : links.entrySet()){
            Node<K> node = link.getKey();
            double linkCost = link.getValue();
            RoutingEntry<K> linkEntry = node.routingTable.get(dest);
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
}
