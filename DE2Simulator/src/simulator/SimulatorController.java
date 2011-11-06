package simulator;

import java.util.Scanner;

public class SimulatorController implements Runnable{

	SystemVerilogSimulator simulator;
	
	public SimulatorController(SystemVerilogSimulator simulator) {
		this.simulator = simulator;
	}

	@Override
	public void run() {
		
		Scanner sc = new Scanner(System.in);
		
		while ( sc.hasNext() ){

			String assignment = sc.nextLine();
			String varName = assignment.split("<=")[0].trim();
			String varValue = assignment.split("<=")[1].trim();
			if ( simulator.setVariableValue(varName, Integer.parseInt(varValue)) ){
				System.out.println("Variable "+varName+" value changed to "+varValue);
			}
			else{
				System.out.println("Variable "+varName+" could not be changed");
			}
			
		}
		
	}

}
