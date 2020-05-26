package uk.co.ramp.covid.simulation.util;

import org.apache.commons.math3.random.RandomDataGenerator;

/** Singleton for managing random number generation */
public class RNG {
    private static RandomDataGenerator rng = null;
    private static int currentSeed;

    private RNG () {};

    public static RandomDataGenerator get() {
        if (rng == null) {
            rng = new RandomDataGenerator();
        }
        return rng;
    }

    public static void seed(int seed) {
        get().reSeed(seed);
        currentSeed = seed;
    }

    public static int getCurrentSeed() { return currentSeed; }
}
