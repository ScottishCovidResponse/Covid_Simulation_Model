/*
 * Code for managing the infection with Covid and for controlling infection
 */

package uk.co.ramp.covid.simulation;

import org.apache.commons.math3.distribution.PoissonDistribution;
import uk.co.ramp.covid.simulation.population.*;

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
    private boolean infected;
    private final Person ccase;

    public Covid(Person ccase) {
        this.meanLatentPeriod = 7;
        this.meanAsymptomaticPeriod = 1;
        this.meanP1 = 5;
        this.meanP2 = 10;

        this.ccase = ccase;
        this.mortalityRate = 0.01;

        this.infCounter = 0;
        this.setPeriods();

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
        this.latentPeriod = new PoissonDistribution(this.meanLatentPeriod).sample();
        this.asymptomaticPeriod = new PoissonDistribution(this.meanAsymptomaticPeriod).sample();
        this.p1 = new PoissonDistribution(this.meanP1).sample();
        this.p2 = new PoissonDistribution(this.meanP2).sample();
        if ((this.ccase instanceof Infant || this.ccase instanceof Child) && Math.random() > 0.02) this.p2 = 0;
        if ((this.ccase instanceof Adult) && Math.random() > 0.15) this.p2 = 0;
        if ((this.ccase instanceof Pensioner) && Math.random() > 0.8) this.p2 = 0;

    }

    // Cycle through the infection for that timestep
    public CStatus stepInfection() {
        this.infCounter++;
        CStatus status = CStatus.LATENT;
        if ((this.latentPeriod * 24) > this.infCounter) {
            this.latent = true;
            this.infected = true;
        } else if (((this.latentPeriod + this.asymptomaticPeriod) * 24) > this.infCounter) {
            this.asymptomatic = true;
            status = CStatus.ASYMPTOMATIC;
            this.infected = true;
        } else if ((this.latentPeriod + this.asymptomaticPeriod + this.p1) * 24 > this.infCounter) {
            this.phase1 = true;
            this.infected = true;
            status = CStatus.PHASE1;

        } else if ((this.latentPeriod + this.asymptomaticPeriod + this.p1 + this.p2) * 24 > this.infCounter) {
            this.phase2 = true;
            this.infected = true;
            double rVal = Math.random();
            if (rVal < this.mortalityRate / 24) {
                this.dead = true;
                this.infected = false;
                status = CStatus.DEAD;
            }
            if (rVal >= this.mortalityRate / 24) {
                status = CStatus.PHASE2;
                this.infected = true;
            }
        } else if ((this.latentPeriod + this.asymptomaticPeriod + this.p1 + this.p2) * 24 <= this.infCounter) {
            this.recovered = true;
            this.infected = false;

            status = CStatus.RECOVERED;
        }
        return status;

    }


}
