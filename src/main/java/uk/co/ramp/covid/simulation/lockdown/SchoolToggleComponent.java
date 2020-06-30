package uk.co.ramp.covid.simulation.lockdown;

import uk.co.ramp.covid.simulation.population.Child;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Time;

/** An example lockdown component showing how we can use dynamic behaviour to swap who goes to school each week */
public class SchoolToggleComponent extends LockdownComponent {
    
    boolean evenFurloughed = true;
    
    public SchoolToggleComponent(Time s, Time e, Population p) {
        super(s, e, p);
    }

    @Override
    protected void start() {
        // tick takes care of the start in this case
    }

    @Override
    protected void end() {
        for (Person p : population.getAllPeople()) {
            if (p instanceof Child) {
              p.unFurlough();
            }
        }
    }

    @Override
    protected void tick(Time t) {
        // Toggle at start of the week
        if (t.getDay() == 0 && t.getHour() == 0) {
            for (Person p : population.getAllPeople()) {
                if (p instanceof Child) {
                    if (evenFurloughed) {
                        if (p.getAge() % 2 == 0) {
                            p.unFurlough();
                        } else {
                            p.forceFurlough();
                        }
                    } else {
                        if (p.getAge() % 2 == 0) {
                            p.forceFurlough();
                        } else {
                            p.unFurlough();
                        }
                    }
                }
            }
            evenFurloughed = !evenFurloughed;
        }
    }

    @Override
    protected String getName() {
        return "School Toggle";
    }
}
