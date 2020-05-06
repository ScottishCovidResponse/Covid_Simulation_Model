package uk.co.ramp.covid.simulation.imported.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomSingleton extends Random {

    private static final Map<Integer, Random> map = new HashMap<>();

    private RandomSingleton() {
        // cannot create via constructor.
    }

    public static Random getInstance(int sid) {

        map.computeIfAbsent(sid, f -> new Random(sid));
        return map.get(sid);

    }
}
