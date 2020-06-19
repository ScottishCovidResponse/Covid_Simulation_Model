package uk.co.ramp.covid.simulation.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class RoundRobinAllocatorTest {

    @Test
    public void testPutGet() {
        RoundRobinAllocator<Integer> r = new RoundRobinAllocator<>();
        r.put(0);
        int x = r.getNext();
        assertEquals(0, x);
    }

    @Test
    public void testGetNothing() {
        RoundRobinAllocator<Integer> r = new RoundRobinAllocator<>();
        Integer x = r.getNext();
        assertNull(x);
    }

    @Test
    public void testPutGetSingleElem() {
        RoundRobinAllocator<Integer> r = new RoundRobinAllocator<>();
        r.put(0);
        int x = r.getNext();
        assertEquals(0, x);

        int y = r.getNext();
        assertEquals(0, y);
    }

    @Test
    public void testPutGetTwoElems() {
        RoundRobinAllocator<Integer> r = new RoundRobinAllocator<>();
        r.put(0);
        r.put(1);
        int x = r.getNext();
        assertEquals(0, x);

        int y = r.getNext();
        assertEquals(1, y);

        int z = r.getNext();
        assertEquals(0, z);
    }
}