package development_v1;
import java.util.*;
public class RunModel {

	public void runTest() {
		Population p = new Population(10000, 3000);
		p.populateHouseholds();
		p.summarisePop();
		p.createMixing();
		p.allocatePeople();
		p.seedVirus(10);
		p.timeStep(100);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
System.out.println("test");
RunModel mModel = new RunModel();
mModel.runTest();
	}

}
