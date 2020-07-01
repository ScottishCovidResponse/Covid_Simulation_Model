package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.util.InvalidParametersException;
import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;

import java.util.Map;
import java.util.Objects;

public class PopulationDistribution {

    public static class SexAge {
        private final Person.Sex sex;
        private final int age;

        public SexAge(Person.Sex sex, int age) {
            this.sex = sex;
            this.age = age;
        }

        public Person.Sex getSex() {
            return sex;
        }

        public int getAge() {
            return age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SexAge sexAge = (SexAge) o;
            return age == sexAge.age &&
                    sex == sexAge.sex;
        }

        @Override
        public int hashCode() {
            return Objects.hash(sex, age);
        }
    }
    
    private final ProbabilityDistribution<SexAge> dist;
    
    public PopulationDistribution() {
        dist = new ProbabilityDistribution<>();
    }
    
    public SexAge sample() {
        return dist.sample();
    }

    /** Reads from data formatted as "m_0_4 -> p" meaning there is a probability p of being a male aged 0-4 */
    public void readFromMap(Map<String, Double> m) {
        for (Map.Entry<String, Double> elem : m.entrySet()) {
            String[] k = elem.getKey().split("_");
            if (k.length != 3) {
                throw new InvalidParametersException("Population data format is invalid");
            }

            Person.Sex s;
            if (k[0].equals("m")) {
                s = Person.Sex.MALE;
            } else if (k[0].equals("f")) {
                s = Person.Sex.FEMALE;
            } else {
                throw new InvalidParametersException("Population data format is invalid. Expected sex = m/f");
            }
            
            int age_l = Integer.parseInt(k[1]);
            int age_r = Integer.parseInt(k[2]);

            if (age_l > age_r) {
                throw new InvalidParametersException("Population data format is invalid. age range start > end");
            }

            int range = age_r - age_l + 1; // Inclusive range
            
            for (int i = 0; i < range; i++) {
                dist.add(elem.getValue() / range, new SexAge(s, age_l + i));
            }
        }
    }
}
