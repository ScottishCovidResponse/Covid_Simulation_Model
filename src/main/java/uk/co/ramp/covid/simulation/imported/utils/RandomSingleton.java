package uk.co.ramp.covid.simulation.imported.utils;

import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomSingleton extends Random {

    private static final Map<Integer, RandomDataGenerator> map = new HashMap<>();

    private RandomSingleton() {
        // cannot create via constructor.
    }

    public static RandomDataGenerator getInstance(int sid) {
        map.computeIfAbsent(sid, f -> {
            RandomDataGenerator rnd = new RandomDataGenerator();
            rnd.reSeed(sid);
            return rnd;
        });
        return map.get(sid);
    }
}

