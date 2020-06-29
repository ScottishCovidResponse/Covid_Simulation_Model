package uk.co.ramp.covid.simulation.util;

import uk.co.ramp.covid.simulation.population.ImpossibleAllocationException;
import uk.co.ramp.covid.simulation.population.ImpossibleWorkerDistributionException;
import uk.co.ramp.covid.simulation.population.Population;

public class PopulationGenerator {
    
    private static final int RETRIES = 20;

    public static Population genValidPopulation(int populationsize) throws CannotGeneratePopulationException {
        Population p = null;
        for (int i = 0; i < RETRIES - 1; i++) {
            try {
                p = new Population(populationsize);
                break;
            } catch (ImpossibleAllocationException | ImpossibleWorkerDistributionException e2) {
                continue;
            }
        }
        
        if (p == null) {
            try {
                p = new Population(populationsize);
            } catch (ImpossibleAllocationException | ImpossibleWorkerDistributionException e2) {
                throw new CannotGeneratePopulationException();
            }

        }
        
        return p;
    }
}
