/*
 * Code for managing the infection with Covid and for controlling infection
 */

package uk.co.ramp.covid.simulation.covid;

import org.apache.commons.math3.random.RandomDataGenerator;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.util.RNG;

public class Covid {
    private boolean latent;
    private boolean asymptomatic;
    private boolean symptomaticCase;
    private boolean isSymptomatic;
    private boolean phase1;
    private boolean phase2;
    private boolean recovered;
    private boolean dead;
    private double latentPeriod;
    private double asymptomaticPeriod;
    private double symptomDelay;
    private double p1;
    private double p2;
    private final double mortalityRate;
    private int infCounter;
    private final Person ccase;
    private final RandomDataGenerator rng;
    
    private final InfectionLog log;

    public Covid(Person ccase) {
        this.rng = RNG.get();
        this.ccase = ccase;
        this.mortalityRate = CovidParameters.get().diseaseParameters.mortalityRate;

        this.infCounter = 0;
        this.setSymptomatic();
        this.setPeriods();

        this.latent = true;
        
        this.log = new InfectionLog();
    }
    
    public void forceSymptomatic(boolean symptoms) { // This is for testing to force the symptomatic status
    	this.symptomaticCase = symptoms;
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
    
    private void setSymptomatic() {
        if(ccase.getAge() >= 60) {
            // This is set to 60 because the probability is from the Diamond Princess where people were aged > 60
            symptomaticCase = CovidParameters.get().diseaseParameters.pSymptomaticCasePensioner.sample();
        } else {
            symptomaticCase = CovidParameters.get().diseaseParameters.pSymptomaticCase.sample();
        }
    }

    // For each infection define the duration of the infection periods
    private void setPeriods() {
        final double meanLatentPeriod = CovidParameters.get().diseaseParameters.meanLatentPeriod;
        final double sdLatentPeriod = CovidParameters.get().diseaseParameters.sdLatentPeriod;
        latentPeriod = Math.exp(rng.nextGaussian(Math.log(meanLatentPeriod), sdLatentPeriod));
        if(!symptomaticCase) {
            final double meanAsymptomaticPeriod = CovidParameters.get().diseaseParameters.meanAsymptomaticPeriod;
            final double sdAsymptomaticPeriod = CovidParameters.get().diseaseParameters.sdAsymptomaticPeriod;
            asymptomaticPeriod = Math.exp(rng.nextGaussian(Math.log(meanAsymptomaticPeriod), sdAsymptomaticPeriod));
        } else {
            final double meanSymptomDelay = CovidParameters.get().diseaseParameters.meanSymptomDelay;
            final double meanSymptomDelaySD = CovidParameters.get().diseaseParameters.meanSymptomDelaySD;
            symptomDelay = latentPeriod - rng.nextGaussian(meanSymptomDelay, meanSymptomDelaySD); // Basically if symptom delay < 0 then the symptoms appear after the infectious period has started; otherwise before
        	if(symptomDelay < 1.0) symptomDelay = 1.0; // There could be the odd instance where we have a negative value here 

            final double meanInfectiousDuration = CovidParameters.get().diseaseParameters.meanInfectiousDuration;
            final double sdInfectiousDuration = CovidParameters.get().diseaseParameters.sdInfectiousDuration;
        	double infectiousPeriod = Math.exp(rng.nextGaussian(Math.log(meanInfectiousDuration), sdInfectiousDuration));

            final double oPhase1Betaa = CovidParameters.get().diseaseParameters.phase1Betaa;
            final double oPhase1Betab = CovidParameters.get().diseaseParameters.phase1Betab;
            p1 = infectiousPeriod * rng.nextBeta(oPhase1Betaa, oPhase1Betab);
            p2 = infectiousPeriod - p1;

            if (ccase.avoidsPhase2(rng.nextUniform(0, 1))) {
                p2 = 0;
            }
        }
    }

    public CStatus stepInfection(Time t) {
    	if(symptomaticCase) {
    	    return this.stepInfectionSymptomatic(t);
        }
    	return this.stepInfectionAsymptomatic();
    }
    
    // Cycle through the infection for that timestep
    public CStatus stepInfectionAsymptomatic() {
        infCounter++;
        CStatus status = CStatus.LATENT;
        if (latentPeriod > infCounter) {
            latent = true;
        } else if (latentPeriod + asymptomaticPeriod > infCounter) {
            asymptomatic = true;
            latent = false;
            status = CStatus.ASYMPTOMATIC;
        } else if (latentPeriod + asymptomaticPeriod <= infCounter) {
            recovered = true;
            asymptomatic = false;
            status = CStatus.RECOVERED;
        }
        return status;
    }

    public CStatus stepInfectionSymptomatic(Time t) {
        infCounter++;
        CStatus status = CStatus.LATENT;
        if (latentPeriod > infCounter) {
            latent = true;
        } else if (latentPeriod + p1 > infCounter) {
            phase1 = true;
            latent = false;
            status = CStatus.PHASE1;
        } else if (latentPeriod + p1 + p2 > infCounter) {
            phase2 = true;
            if(!isSymptomatic) { // This if statement is needed because the case could or could not have reached this point wihtout symptoms
            	isSymptomatic = true; 
            	log.registerSymptomatic(t);
            	ccase.getHome().isolate();
            }
            phase1 = false;
            double rVal = rng.nextUniform(0, 1);
            if (rVal < mortalityRate / 24) {
                dead = true;
                phase2 = false;
                status = CStatus.DEAD;
            } else {
                status = CStatus.PHASE2;
            }
        } else if (latentPeriod + p1 + p2 <= infCounter) {
            recovered = true;
            phase1 = false;
            phase2 = false;
            isSymptomatic = false;
            status = CStatus.RECOVERED;

        }
        if(symptomDelay < infCounter && !recovered) {
            // This check ensures we don't isolate twice with the same case
            if (!isSymptomatic) {
                isSymptomatic = true;
                log.registerSymptomatic(t);
                ccase.getHome().isolate();
            }
        }
        return status;
    }

    public double getLatentPeriod() {
        return latentPeriod;
    }

    public double getAsymptomaticPeriod() {
        return asymptomaticPeriod;
    }
    
    public boolean isSymptomatic() {
    	return isSymptomatic;
    }

    public double getP1() {
        return p1;
    }
    
    public double getP2() {
        return p2;
    }

    public double getSymptomDelay() {
        return symptomDelay;
    }

    public double getTransAdjustment() {
        if (asymptomatic) {
            return CovidParameters.get().diseaseParameters.aSymptomaticTransAdjustment;
        } else {
            return CovidParameters.get().diseaseParameters.symptomaticTransAdjustment;
        }
    }

    public InfectionLog getInfectionLog() {
        return log;
    }
}
