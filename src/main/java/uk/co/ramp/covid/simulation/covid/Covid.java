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
    private boolean symptomaticCase;
    private boolean isSymptomatic;
    private CStatus status;
    private double latentPeriod;
    private double asymptomaticPeriod;
    private double symptomDelay;
    private double p1;
    private double p2;
    private final double caseMortalityRate;
    private int infCounter;
    private final Person ccase;
    private final RandomDataGenerator rng;
    private boolean dies;
    
    private final InfectionLog log;

    public Covid(Person ccase) {
        this.rng = RNG.get();
        this.ccase = ccase;
        this.caseMortalityRate = CovidParameters.get().diseaseParameters.caseMortalityRate;

        this.infCounter = 0;
        this.setSymptomatic();
        this.setPeriods();

        this.status = CStatus.LATENT;
        
        this.log = new InfectionLog();
    }
    
    public void forceSymptomatic(boolean symptoms) { // This is for testing to force the symptomatic status
    	this.symptomaticCase = symptoms;
    	this.setPeriods();
    }

    public boolean isDead() {
        return status == CStatus.DEAD;
    }
     
    private void setSymptomatic() {
        if (ccase.getAge() >= 70) {
            // This is set to 70 and 20 because the paper form LSHTM that informed this use these cut-offs
            symptomaticCase = CovidParameters.get().diseaseParameters.pSymptomaticCaseOver70.sample();
        } else if (ccase.getAge() < 70 && ccase.getAge() > 20) {
            symptomaticCase = CovidParameters.get().diseaseParameters.pSymptomaticCaseOver21.sample();
        } else {
            symptomaticCase = CovidParameters.get().diseaseParameters.pSymptomaticCaseUnder21.sample();
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
            
            if(p2 > 0) {
                final boolean goesToHospital;

            	dies = rng.nextUniform(0,  1) < caseMortalityRate * ccase.getCovidMortalityAgeAdjustment();
            	if(dies) {
            		goesToHospital = CovidParameters.get().diseaseParameters.pFatalityGoesToHospital.sample();
            	}
            	else {
            		goesToHospital = CovidParameters.get().diseaseParameters.pSurvivorGoesToHospital.sample();
            	}
            	if(goesToHospital) {
            		ccase.setWillBeHospitalised();
            	}

            }
        }
    }

    public void stepInfection(Time t) {
    	if(symptomaticCase) {
    	    stepInfectionSymptomatic(t);
        } else {
            stepInfectionAsymptomatic();
        }
    }
    
    // Cycle through the infection for that timestep
    public void stepInfectionAsymptomatic() {
        infCounter++;
        if (latentPeriod > infCounter) {
            status = CStatus.LATENT;
        } else if (latentPeriod + asymptomaticPeriod > infCounter) {
            status = CStatus.ASYMPTOMATIC;
        } else if (latentPeriod + asymptomaticPeriod <= infCounter) {
            status = CStatus.RECOVERED;
        }
    }

    public void stepInfectionSymptomatic(Time t) {
        infCounter++;
        if (latentPeriod > infCounter) {
            status = CStatus.LATENT;
        } else if (latentPeriod + p1 > infCounter) {
            status = CStatus.PHASE1;
        } else if (latentPeriod + p1 + p2 > infCounter) {
            if(!isSymptomatic) { // This if statement is needed because the case could or could not have reached this point without symptoms
            	isSymptomatic = true; 
            	log.registerSymptomatic(t);
            }
            status = CStatus.PHASE2;
        } else if (latentPeriod + p1 + p2 <= infCounter) {
            if(dies) {
            	status = CStatus.DEAD;
            } else {
            	isSymptomatic = false;
            	status = CStatus.RECOVERED;
            }
        }
        if(symptomDelay < infCounter && status != CStatus.RECOVERED) {
            // This check ensures we don't isolate twice with the same case
            if (!isSymptomatic) {
                isSymptomatic = true;
                log.registerSymptomatic(t);
            }
        }
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
        if (status == CStatus.ASYMPTOMATIC) {
            return CovidParameters.get().diseaseParameters.asymptomaticTransAdjustment;
        } else {
            return CovidParameters.get().diseaseParameters.symptomaticTransAdjustment;
        }
    }

    public InfectionLog getInfectionLog() {
        return log;
    }

    public CStatus getStatus() {
        return status;
    }
}
