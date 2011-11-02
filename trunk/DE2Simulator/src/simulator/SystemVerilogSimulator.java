package simulator;

import hardware_model.HardwareModel;
import hdl_binding.SystemVerilogParser;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;


public class SystemVerilogSimulator {

	public static void main(String[] args) throws Exception{
		
		Set<String> modules = new HashSet<String>();
		
		Scanner sc = new Scanner(System.in);
		StringBuilder module = new StringBuilder();
		while ( sc.hasNext() ){
			String nextLine = sc.nextLine();
			if ( nextLine.trim().equals("ITSOK") ){
				break;
			}
			module.append(nextLine);
			module.append("/n");
			System.out.println("appended");
		}
		modules.add(new String(module));
		SystemVerilogParser parser = new SystemVerilogParser(modules);
		HardwareModel model = parser.getGeneratedHardwareModel();
		System.out.println(model);
	}
	
//MOST COMPLEX EXAMPLE THAT WORKS?
	
/*
module ula #() (

input logic A,
input logic B,
output logic C
);

always_comb B <= X + A;

logic [5:0]X;

always_comb X <= 3'b001;

logic Y[0:12];

endmodule
 */
	
	
}
