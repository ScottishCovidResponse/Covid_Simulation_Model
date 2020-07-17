package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

public class DiseaseParameters {
    public Double meanLatentPeriod = null;
    public Double sdLatentPeriod = null;
    public Double meanAsymptomaticPeriod = null;
    public Double sdAsymptomaticPeriod = null;
    public Probability pSymptomaticCaseChild = null;
    public Probability pSymptomaticCaseAdult = null;
    public Probability pSymptomaticCasePensioner = null;
    public Double meanSymptomDelay = null;
    public Double meanSymptomDelaySD = null;
    public Double meanInfectiousDuration = null;
    public Double sdInfectiousDuration = null;
    public Double phase1Betaa = null;
    public Double phase1Betab = null;
    public Double asymptomaticTransAdjustment = null;
    public Double symptomaticTransAdjustment = null;
    public Double caseMortalityBase = null;
    public Double caseMortalityRate = null;
    public Double childProgressionPhase2 = null;
    public Double adultProgressionPhase2 = null;
    public Double pensionerProgressionPhase2 = null;

    /** Probabilities that a case goes to hospital when they eventually survive/die */
    public Probability pSurvivorGoesToHospital = null;
    public Probability pFatalityGoesToHospital = null;
}
