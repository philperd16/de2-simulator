package simulator;

import hardware_model.Assignment;
import hardware_model.Combinational;
import hardware_model.ModuleDefinition;
import hardware_model.Variable;
import hdl_binding.ParsingException;
import hdl_binding.SystemVerilogParser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class SystemVerilogSimulator {

	Parser parser;
	Map<ModuleDefinition, Set<Variable>> model;
	Map<ModuleDefinition, Set<InstructionThread>> combinationalThreads;
	
	public SystemVerilogSimulator(){
		parser = new SystemVerilogParser();
		model = new HashMap<ModuleDefinition, Set<Variable>>();
	}

	public void prepareSimulation(Set<String> modules) throws ParsingException{
		parser.parseHDLCode(modules);
		
		for ( ModuleDefinition module : parser.getGeneratedHardwareModel().getDefinedModules() ){
			
			model.put(module, new HashSet<Variable>(module.getAllVariables()));
			
			for ( Combinational combinational : module.getCombinationalBlocks() ){
				for ( Assignment instruction : combinational.getInstructions() ){
					Set<InstructionThread> threads = new HashSet<InstructionThread>();
					InstructionThread combinationalAssignment = new InstructionThread(instruction, module.getAllVariables());
					threads.add(combinationalAssignment);
				}
			}
		}
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
