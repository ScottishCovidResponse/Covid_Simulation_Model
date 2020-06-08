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
    
    public static int generateRandomSeed() {
        // The default generator is seeded with System.currentTimeMillis() + System.identityHashCode(this)).
        return (new RandomDataGenerator()).nextInt(0, Integer.MAX_VALUE);
    }
}
