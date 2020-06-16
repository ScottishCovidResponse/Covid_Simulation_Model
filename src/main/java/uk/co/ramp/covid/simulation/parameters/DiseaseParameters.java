package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

public class DiseaseParameters {
    public Double meanLatentPeriod = null;
    public Double sdLatentPeriod = null;
    public Double meanAsymptomaticPeriod = null;
    public Probability pSymptomaticCase = null;
    public Double meanSymptomDelay = null;
    public Double meanSymptomDelaySD = null;
    public Double meanInfectiousDuration = null;
    public Double phase1Betaa = null;
    public Double phase1Betab = null;
    public Double aSymptomaticTransAdjustment = null;
    public Double symptomaticTransAdjustment = null;
    public Double mortalityRate = null;
    public Double childProgressionPhase2 = null;
    public Double adultProgressionPhase2 = null;
    public Double pensionerProgressionPhase2 = null;
}
