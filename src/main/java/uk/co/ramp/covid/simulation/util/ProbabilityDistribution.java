package uk.co.ramp.covid.simulation.util;

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
    private final List<ProbPair> pmap;
    private double totalProb;
    private final double EPSILON = 0.0000001;

    public ProbabilityDistribution() {
        pmap = new ArrayList<>();
        totalProb = 0.0;
    }

    /**
     * adds a element val to be draw from the distribution with probability prob
     */
    public void add(double prob, T val) {
        totalProb += prob;
        assert totalProb <= 1 + EPSILON : "Trying to create probability distribution with probability total > 1";

        pmap.add(new ProbPair(prob, val));
        pmap.sort(Collections.reverseOrder());
    }

    public void add(Probability prob, T val) {
        add(prob.asDouble(), val);
    }

    /** sample the current probability distribution. Returns null on failure */
    public T sample() {
        assert isValid()
                : "Trying to sample from a distribution that does not have a total probability of 1";

        double rand = RNG.get().nextUniform(0, 1);
        for (ProbPair p : pmap) {
            if (rand <= p.prob) {
                return p.val;
            }
            rand = rand - p.prob;
        }
        return null;
    }

    public List<T> toList() {
        List<T> l = new ArrayList<>();
        for (ProbPair p : pmap) {
            l.add(p.val);
        }
        return l;
    }

    /** Determine if the total probability equals 1, i.e. the distribution is valid to use */
    public boolean isValid() {
        return totalProb > 1 - EPSILON || totalProb < 1 + EPSILON;
    }

}
