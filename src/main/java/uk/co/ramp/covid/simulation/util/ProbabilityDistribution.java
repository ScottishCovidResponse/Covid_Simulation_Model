package uk.co.ramp.covid.simulation.util;

import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.PopulationParameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Helper class to make it easier to draw elements of a generic type from a given distribution */
public class ProbabilityDistribution<T> {
    // Java doesn't have built-in Pairs, so we need to define our own
    class ProbPair implements Comparable<ProbPair> {
        public double prob;
        public T val;

        public ProbPair(double p, T v) {
            prob = p;
            val = v;
        }

        public int compareTo(ProbPair p) {
            return Double.compare(prob, p.prob);
        }
    }

    //  pmap works as an associative list to allow us to easy assign probabilities to arbitrary types
    private List<ProbPair> pmap;
    private double totalProb;
    private final double EPSILON = 0.0000001;

    public ProbabilityDistribution() {
        pmap = new ArrayList();
        totalProb = 0.0;
    }

    /**
     * adds a element val to be draw from the distribution with probability prob
     */
    public void add(double prob, T val) {
        totalProb += prob;
        assert totalProb <= 1 + EPSILON : "Trying to create probability distribution with probability total > 1";

        pmap.add(new ProbPair(prob, val));
        Collections.sort(pmap, Collections.reverseOrder());
    }

    /** sample the current probability distribution. Returns null on failure */
    public T sample() {
        assert totalProb > 1 - EPSILON || totalProb < 1 + EPSILON
                : "Trying to sample from a distribution that does not have a total probability of 1";

        double rand = Math.random();
        for (ProbPair p : pmap) {
            if (rand <= p.prob) {
                return p.val;
            }
            rand = rand - p.prob;
        }
        return null;
    }
}