/*
 * Code for managing the infection with Covid and for controlling infection
 */

package uk.co.ramp.covid.simulation.covid;

import org.apache.commons.math3.random.RandomDataGenerator;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.util.Probability;
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
    private final double meanLatentPeriod;
    private final double sdLatentPeriod;
    private final double meanAsymptomaticPeriod;
    private final double sdAsymptomaticPeriod;
    private final Probability pSymptomsChild;
    private final Probability pSymptomsAdult;
    private final Probability pSymptomsPensioner;
    private final double meanSymptomDelay;
    private final double meanSymptomDelaySD;    
    private final double meanInfectiousDuration;
    private final double sdInfectiousDuration;
    private final double oPhase1Betaa;
    private final double oPhase1Betab;
    private final double asymptomaticTransAdjustment;
    private final double symptomaticTransAdjustment;    
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
        this.meanLatentPeriod = CovidParameters.get().diseaseParameters.meanLatentPeriod;
        this.sdLatentPeriod = CovidParameters.get().diseaseParameters.sdLatentPeriod;
        this.meanAsymptomaticPeriod = CovidParameters.get().diseaseParameters.meanAsymptomaticPeriod;
        this.sdAsymptomaticPeriod = CovidParameters.get().diseaseParameters.sdAsymptomaticPeriod;
        this.pSymptomsChild = CovidParameters.get().diseaseParameters.pSymptomaticCaseChild;
        this.pSymptomsAdult = CovidParameters.get().diseaseParameters.pSymptomaticCaseAdult;
        this.pSymptomsPensioner = CovidParameters.get().diseaseParameters.pSymptomaticCasePensioner;
        this.meanSymptomDelay = CovidParameters.get().diseaseParameters.meanSymptomDelay;
        this.meanSymptomDelaySD = CovidParameters.get().diseaseParameters.meanSymptomDelaySD;
        this.meanInfectiousDuration = CovidParameters.get().diseaseParameters.meanInfectiousDuration;
        this.sdInfectiousDuration = CovidParameters.get().diseaseParameters.sdInfectiousDuration;
        this.oPhase1Betaa = CovidParameters.get().diseaseParameters.phase1Betaa;
        this.oPhase1Betab = CovidParameters.get().diseaseParameters.phase1Betab;
        this.asymptomaticTransAdjustment = CovidParameters.get().diseaseParameters.aSymptomaticTransAdjustment;
        this.symptomaticTransAdjustment = CovidParameters.get().diseaseParameters.symptomaticTransAdjustment;
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
        symptomaticCase = pSymptomsAdult.sample();
        if(ccase.getAge() <= 20) symptomaticCase = pSymptomsChild.sample(); // This is set to 60 because the probability is from the Diamond Princess where people were aged > 60 
        else if(ccase.getAge() >= 70) symptomaticCase = pSymptomsPensioner.sample(); // This is set to 60 because the probability is from the Diamond Princess where people were aged > 60 
    }

    // For each infection define the duration of the infection periods
    private void setPeriods() {
        latentPeriod = Math.exp(rng.nextGaussian(Math.log(meanLatentPeriod), sdLatentPeriod));
        if(!symptomaticCase) {
            asymptomaticPeriod = Math.exp(rng.nextGaussian(Math.log(meanAsymptomaticPeriod), sdAsymptomaticPeriod));
        } else {
        	symptomDelay = latentPeriod - rng.nextGaussian(meanSymptomDelay, meanSymptomDelaySD); // Basically if symptom delay < 0 then the symptoms appear after the infectious period has started; otherwise before
        	if(symptomDelay < 1.0) symptomDelay = 1.0; // There could be the odd instance where we have a negative value here 
        
        	double infectiousPeriod = Math.exp(rng.nextGaussian(Math.log(meanInfectiousDuration), sdInfectiousDuration));
        
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
        if ((latentPeriod) > infCounter) {
            latent = true;
        } else if (((latentPeriod + asymptomaticPeriod)) > infCounter) {
            asymptomatic = true;
            latent = false;
            status = CStatus.ASYMPTOMATIC;
        } else if ((latentPeriod + asymptomaticPeriod) <= infCounter) {
            recovered = true;
            asymptomatic = false;
            status = CStatus.RECOVERED;
        }
        return status;
    }

    public CStatus stepInfectionSymptomatic(Time t) {
        infCounter++;
        CStatus status = CStatus.LATENT;
        if ((latentPeriod) > infCounter) {
            latent = true;
        } else if ((latentPeriod + p1) > infCounter) {
            phase1 = true;
            latent = false;
            status = CStatus.PHASE1;
        } else if ((latentPeriod + p1 + p2) > infCounter) {
            phase2 = true;
            if(!isSymptomatic) { // This if statement is needed because the case could or could not have reached this point wihtout symptoms
            	isSymptomatic = true; 
            	log.registerSymptomatic(t);
            	ccase.getHome().isolate();
            }
            phase1 = false;
            double rVal = rng.nextUniform(0, 1);
            if (rVal < mortalityRate / 24 * ccase.getCovidMortalityAgeAdjustment()) {
                dead = true;
                phase2 = false;
                status = CStatus.DEAD;
            }
            if (rVal >= mortalityRate / 24 * ccase.getCovidMortalityAgeAdjustment()) {
                status = CStatus.PHASE2;
            }
        } else if ((latentPeriod + p1 + p2) <= infCounter) {
            recovered = true;
            phase1 = false;
            phase2 = false;
            isSymptomatic = false;
            status = CStatus.RECOVERED;

        }
        if((symptomDelay) < infCounter && !recovered) {
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
    	double transAdjustment = 0.0;
    	if(asymptomatic) transAdjustment = this.asymptomaticTransAdjustment;
    	else transAdjustment = this.symptomaticTransAdjustment;
    	
    	return transAdjustment;
    	
    }

    public InfectionLog getInfectionLog() {
        return log;
    }
}
