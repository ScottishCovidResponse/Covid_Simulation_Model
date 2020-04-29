/*
 * Paul Bessell. 
 * This Class initialises the model by generating a Population object and simulating Covid spread through the Populaiton 
*/
package development_v1;
import java.util.*;
public class RunModel {

	public void runTest() {
		System.out.println((1.0/7.0)/24.0);
		Population p = new Population(10000, 3000);
		p.populateHouseholds();
		p.summarisePop();
		p.createMixing();
		p.allocatePeople();
		p.seedVirus(10);
		p.setLockdown(40, 100);
		p.timeStep(300);
	}
	
	public void runBaseline() throws Exception { // Run and output the baseline scenarios
		Population p = new Population(50000, 15000);
		p.populateHouseholds();
		p.summarisePop();
		p.createMixing();
		p.allocatePeople();
		p.seedVirus(10);
		//p.setLockdown(40, 100);
		ReadWrite rw = new ReadWrite("ModelOutputs//Baseline20200429//BaselineOut.csv");
		rw.openWritemodel();
		int nIter = 100;
		for(int i = 1; i <= nIter; i++) {
			Vector vNext = p.timeStep(365);
			for(int k = 0; k < vNext.size(); k++) rw.writemodel(i, (String) vNext.elementAt(i)); 
		}
		
	}

	public void runLockdown() throws Exception { // Run and output the baseline scenarios
		Population p = new Population(50000, 15000);
		p.populateHouseholds();
		p.summarisePop();
		p.createMixing();
		p.allocatePeople();
		p.seedVirus(10);
		p.setLockdown(45, 87);
		ReadWrite rw = new ReadWrite("ModelOutputs//Lockdown20200429//Lockdown_45_87.csv");
		rw.openWritemodel();
		int nIter = 100;
		for(int i = 1; i <= nIter; i++) {
			Vector vNext = p.timeStep(365);
			for(int k = 0; k < vNext.size(); k++) rw.writemodel(i, (String) vNext.elementAt(i)); 
		}
		
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
System.out.println("test");
RunModel mModel = new RunModel();
//mModel.runTest();
mModel.runBaseline();
mModel.runLockdown();
	}

}
