package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Places;
import uk.co.ramp.covid.simulation.population.Shifts;
import uk.co.ramp.covid.simulation.util.RoundRobinAllocator;

public class Shop extends CommunalPlace {
    
    private RoundRobinAllocator<Shifts> shifts;

    public Shop(Size s) {
        super(s);
        transAdjustment = PopulationParameters.get().buildingProperties.shopTransmissionConstant;
        keyProb = PopulationParameters.get().buildingProperties.pShopKey;
        if (keyProb.sample()) keyPremises = true;
        setOpeningHours();
    }
    
    private void setOpeningHours() {
        shifts = new RoundRobinAllocator<>();
        if (size == Size.SMALL) {
            times = OpeningTimes.nineFiveAllWeek();
            shifts.put(new Shifts(9,17, 0, 1, 2));
            shifts.put(new Shifts(9,17, 3, 4, 5, 6));
        } else {
            times = OpeningTimes.eightTenAllWeek();
            shifts.put(new Shifts(8,15, 0, 1, 2));
            shifts.put(new Shifts(15,22, 0, 1, 2));
            shifts.put(new Shifts(8,15, 3, 4, 5, 6));
            shifts.put(new Shifts(15,22, 3, 4, 5, 6));
        }
    }

    @Override
    public Shifts getShifts() {
        nStaff++;
        return shifts.getNext();
    }

    public void sendHome(Time t) {
       
        for (Person p : getPeople()) {
            // People may have already left if their family has
            if (p.hasMoved()) {
                continue;
            }

            if (p.worksNextHour(this, t, false)) {
                continue;
            }

            // Under certain conditions we must go home, e.g. if there is a shift starting soon
            if (p.mustGoHome(t)) {
                p.returnHome(this);
                sendFamilyHome(p, this, t);
            }
            else if (PopulationParameters.get().buildingProperties.pLeaveShop.sample()
                    || !times.isOpenNextHour(t)) {
                p.returnHome(this);
                sendFamilyHome(p, this, t);
            }
        }
    }

    @Override
    public void reportInfection(Time t, Person p, DailyStats s) {
        if (p.isWorking(this, t)) {
            s.incInfectionsShopWorker();
        } else {
            s.incInfectionsShopVisitor();
        }
    }

    @Override
    public void determineMovement(Time t, boolean lockdown, Places places) {
        movePhase2(t, places);
        moveShifts(t, lockdown);
        sendHome(t);
        remainInPlace();
    }

    @Override
    public boolean isFullyStaffed() {
        if (size == Size.SMALL)
            return nStaff >= 2;
        else {
            return nStaff >= 4;
        }
    }
}
