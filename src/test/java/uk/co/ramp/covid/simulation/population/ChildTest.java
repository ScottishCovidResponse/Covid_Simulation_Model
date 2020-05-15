package uk.co.ramp.covid.simulation.population;

import com.google.gson.JsonParseException;
import org.junit.Test;
import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import java.io.IOException;
import static org.junit.Assert.assertTrue;

public class ChildTest {

    @Test
    public void testChild() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("parameters/example_params.json");
        new RunModel(123);
        Child child = new Child();
        assertTrue("Child not at school", child.isSchool());
    }
}