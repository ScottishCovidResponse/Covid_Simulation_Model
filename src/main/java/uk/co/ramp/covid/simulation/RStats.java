package uk.co.ramp.covid.simulation;

import uk.co.ramp.covid.simulation.covid.Covid;
import uk.co.ramp.covid.simulation.covid.InfectionLog;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Population;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/** RStats handles generating/outputting the R naught statistics */
public class RStats {

    private final Population population;

    public RStats(Population p) {
        this.population = p;
    }

    private Double getMeanRGeneric(int absDay, BiFunction<Integer, Integer, Boolean> cmp) {
        double nInfectors = 0;
        double nSecondaries = 0;
        for (Person p : population.getAllPeople()) {
            Covid virus = p.getcVirus();
            if (virus != null) {
                if (cmp.apply(virus.getInfectionLog().getInfectionTime().getAbsDay(), absDay)) {
                    nInfectors++;
                    nSecondaries += virus.getInfectionLog().getSecondaryInfections().size();
                }
            }
        }

        if (nSecondaries > 0) {
            return nSecondaries /  nInfectors;
        }

        return null;
    }

    /** Returns the mean R for a given day */
    public Double getMeanR(int absDay) {
        return getMeanRGeneric(absDay, (a, b) -> a == b);
    }

    /** Returns the mean R up to a given day */
    public Double getMeanRBefore(int absDay) {
        return getMeanRGeneric(absDay, (a, b) -> a <= b);
    }

    /** Returns the mean generation time for a given day */
    public Double getMeanGenerationTime(int absDay) {
        List<Integer> generationTimes = new ArrayList<>();

        for (Person p : population.getAllPeople()) {
            Covid virus = p.getcVirus();
            if (virus != null) {
                if (virus.getInfectionLog().getInfectionTime().getAbsDay() == absDay) {
                    for (InfectionLog.SecondaryInfection inf : virus.getInfectionLog().getSecondaryInfections()) {
                        generationTimes.add(inf.getInfectionTime().getAbsDay() - absDay);
                    }
                }
            }
        }

        double res = generationTimes.stream().mapToInt(v -> v).average().orElse(-1);
        if (res == -1) {
            return null;
        }

        return res;
    }
}