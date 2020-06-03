package uk.co.ramp.covid.simulation.testutil;

import uk.co.ramp.covid.simulation.population.ImpossibleAllocationException;
import uk.co.ramp.covid.simulation.population.ImpossibleWorkerDistributionException;
import uk.co.ramp.covid.simulation.population.Population;

public class PopulationGenerator {
    
    private static final int RETRIES = 20;

    public static Population genValidPopulation(int populationsize) {
        Population p = null;
        for (int i = 0; i < RETRIES; i++) {
            try {
                p = new Population(populationsize);
            } catch (ImpossibleAllocationException | ImpossibleWorkerDistributionException e2) {
                continue;
            }
        }
        return p;
    }
}
