/*
 * Code for managing the infection with Covid and for controlling infection
 */

package uk.co.ramp.covid.simulation;

import org.apache.commons.math3.random.RandomDataGenerator;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.util.RNG;

public class Covid {
    private boolean latent;
    private boolean asymptomatic;
    private boolean phase1;
    private boolean phase2;
    private boolean recovered;
    private boolean dead;
    private final int meanLatentPeriod;
    private final int meanAsymptomaticPeriod;
    private final int meanP1;
    private final int meanP2;
    private int latentPeriod;
    private double asymptomaticPeriod;
    private double p1;
    private double p2;
    private final double mortalityRate;
    private int infCounter;
    private final Person ccase;
    private final RandomDataGenerator rng;

    public Covid(Person ccase) {
        this.rng = RNG.get();
        this.meanLatentPeriod = CovidParameters.get().getMeanLatentPeriod();
        this.meanAsymptomaticPeriod = CovidParameters.get().getMeanAsymptomaticPeriod();
        this.meanP1 = CovidParameters.get().getMeanPhase1DurationMild();
        this.meanP2 = CovidParameters.get().getMeanPhase1DurationSevere();

        this.ccase = ccase;
        this.mortalityRate = CovidParameters.get().getMortalityRate();

        this.infCounter = 0;
        this.setPeriods();

        this.latent = true;
    }

    public boolean isLatent() {
        return latent;
    }

    public boolean isAsymptomatic() {
        return asymptomatic;
    }

    public boolean isPhase1() {
        return phase1;
    }

    public boolean isPhase2() {
        return phase2;
    }

    public boolean isRecovered() {
        return recovered;
    }

    public boolean isDead() {
        return dead;
    }

    // For each infection define the duration of the infection periods
    private void setPeriods() {
        latentPeriod = (int) rng.nextPoisson(meanLatentPeriod);
        asymptomaticPeriod = (int) rng.nextPoisson(meanAsymptomaticPeriod);

        p1 = rng.nextPoisson(meanP1);

        if (ccase.avoidsPhase2(rng.nextUniform(0, 1))) {
            p2 = 0;
        } else {
            p2 = rng.nextPoisson(meanP2);
        }
    }

    // Cycle through the infection for that timestep
    public CStatus stepInfection() {
        this.infCounter++;
        CStatus status = CStatus.LATENT;
        if ((this.latentPeriod * 24) > this.infCounter) {
            this.latent = true;
        } else if (((this.latentPeriod + this.asymptomaticPeriod) * 24) > this.infCounter) {
            this.asymptomatic = true;
            status = CStatus.ASYMPTOMATIC;
        } else if ((this.latentPeriod + this.asymptomaticPeriod + this.p1) * 24 > this.infCounter) {
            this.phase1 = true;
            status = CStatus.PHASE1;

        } else if ((this.latentPeriod + this.asymptomaticPeriod + this.p1 + this.p2) * 24 > this.infCounter) {
            this.phase2 = true;
            double rVal = rng.nextUniform(0, 1);
            if (rVal < this.mortalityRate / 24) {
                this.dead = true;
                status = CStatus.DEAD;
            }
            if (rVal >= this.mortalityRate / 24) {
                status = CStatus.PHASE2;
            }
        } else if ((this.latentPeriod + this.asymptomaticPeriod + this.p1 + this.p2) * 24 <= this.infCounter) {
            this.recovered = true;

            status = CStatus.RECOVERED;
        }
        return status;

    }


}
