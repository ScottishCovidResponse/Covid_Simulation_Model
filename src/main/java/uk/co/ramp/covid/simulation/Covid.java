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
    private int latentPeriod;
    private double asymptomaticPeriod;
    private double p1;
    private double p2;
    private final double mortalityRate;
    private int infCounter;
    private final Person ccase;
    
    private static PoissonDistribution latentPeriodDistribution;
    private static PoissonDistribution asymptomaticPeriodDistribution;
    private static PoissonDistribution p1Distribution;
    private static PoissonDistribution p2Distribution;

    private static void getDistributions() {
        if (latentPeriodDistribution == null || asymptomaticPeriodDistribution == null || p1Distribution == null
                || p2Distribution == null) {
            int meanLatentPeriod = CovidParameters.get().getMeanLatentPeriod();
            int meanAsymptomaticPeriod = CovidParameters.get().getMeanAsymptomaticPeriod();
            int meanP1 = CovidParameters.get().getMeanPhase1DurationMild();
            int meanP2 = CovidParameters.get().getMeanPhase1DurationSevere();

            latentPeriodDistribution = new PoissonDistribution(meanLatentPeriod);
            asymptomaticPeriodDistribution = new PoissonDistribution(meanAsymptomaticPeriod);
            p1Distribution = new PoissonDistribution(meanP1);
            p2Distribution = new PoissonDistribution(meanP2);
        }
    }

    public Covid(Person ccase) {        
        this.ccase = ccase;
        this.mortalityRate = CovidParameters.get().getMortalityRate();

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
        getDistributions();

        this.latentPeriod = latentPeriodDistribution.sample();
        this.asymptomaticPeriod = asymptomaticPeriodDistribution.sample();
        this.p1 = p1Distribution.sample();
        this.p2 = p2Distribution.sample();
        if ((this.ccase instanceof Infant || this.ccase instanceof Child)
                && Math.random() > CovidParameters.get().getChildProgressionPhase2()) {
            this.p2 = 0;
        }
        if ((this.ccase instanceof Adult)
                && Math.random() > CovidParameters.get().getAdultProgressionPhase2()) {
            this.p2 = 0;
        }
        if ((this.ccase instanceof Pensioner)
                && Math.random() > CovidParameters.get().getPensionerProgressionPhase2()) {
            this.p2 = 0;
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
            double rVal = Math.random();
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
