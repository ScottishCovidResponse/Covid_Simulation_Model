package uk.co.ramp.covid.simulation.util;

import java.util.ArrayList;
import java.util.List;

/** Simple class to return elements of a container in a round-robin fashion
 *  Used primarily to assign shifts to workers
 */
public class RoundRobinAllocator<T> {
    private List<T> data;
    private int next;
    
    public RoundRobinAllocator() {
        data = new ArrayList<>();
        next = -1;
    }
    
    public void put(T e) {
        data.add(e);
        if (next == -1) {
            next = 0;
        }
    }
    
    public T getNext() {
        if (next == -1) {
            return null;
        } 
        
        T res = data.get(next);
        next = (next + 1) % data.size();
        return res;
    }
}