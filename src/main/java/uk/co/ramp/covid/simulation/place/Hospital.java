package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Shifts;
import uk.co.ramp.covid.simulation.util.RoundRobinAllocator;

public class Hospital extends CommunalPlace {
    
    private RoundRobinAllocator<Shifts> shifts;

    public Hospital() {
        this(Size.UNKNOWN);
    }

    public Hospital(Size s) {
        super(s);
        transProb = PopulationParameters.get().getpBaseTrans() * PopulationParameters.get().getpHospitalTrans();
        keyProb = PopulationParameters.get().getpHospitalKey();
        times = OpeningTimes.twentyfourSeven();
        if (rng.nextUniform(0, 1) > keyProb) keyPremises = true;
        allocateShifts();
    }

    private void allocateShifts() {
        shifts = new RoundRobinAllocator<>();
        shifts.put(new Shifts(0,12, 0, 1, 2));
        shifts.put(new Shifts(12,0, 0, 1, 2));
        shifts.put(new Shifts(0,12, 3, 4, 5, 6));
        shifts.put(new Shifts(12,0, 3, 4, 5, 6));
    }

    @Override
    public Shifts getShifts() {
        nStaff++;
        return shifts.getNext();
    }

    @Override
    public boolean isFullyStaffed() {
        return nStaff >= 4;
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionHospital();
    }
}
