package uk.co.ramp.covid.simulation.util;

import org.apache.commons.math3.random.RandomDataGenerator;

/** Singleton for managing random number generation */
public class RNG {
    private static RandomDataGenerator rng = null;

    private RNG () {};

    public static RandomDataGenerator get() {
        if (rng == null) {
            rng = new RandomDataGenerator();

            // Use a fixed seed for tests
            // - seed() will always be called to override this in non-test runs  
            rng.reSeed(0);
        }
        return rng;
    }

    public static void seed(int seed) {
        get().reSeed(seed);
    }
}
