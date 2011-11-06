package simulator;

import hardware_model.Assignment;
import hardware_model.CodeBlock;
import hardware_model.Instruction;
import hardware_model.Instruction.InstructionType;
import hardware_model.ModuleDefinition;
import hardware_model.Variable;
import hdl_binding.Parser;
import hdl_binding.ParsingException;
import hdl_binding.SystemVerilogParser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;

public class SystemVerilogSimulator {

	private Parser parser;
	private Map<ModuleDefinition, Set<Variable>> model;
	private Map<ModuleDefinition, Set<InstructionThread>> combinationalThreads;
	private Timer timer;
	private static SystemVerilogSimulator systemVerilogSimulator;
	
	private SystemVerilogSimulator() {
		parser = new SystemVerilogParser();
		model = new HashMap<ModuleDefinition, Set<Variable>>();
		combinationalThreads = new HashMap<ModuleDefinition, Set<InstructionThread>>();
	}

	public void prepareSimulation(Set<String> modules) throws ParsingException {
		parser.parseHDLCode(modules);

		for (ModuleDefinition module : parser.getGeneratedHardwareModel()
				.getDefinedModules()) {

			model.put(module, new HashSet<Variable>(module.getAllVariables()));

			for (CodeBlock combinational : module.getCombinationalBlocks()) {
				Set<InstructionThread> threads = new HashSet<InstructionThread>();
				for (Instruction instruction : combinational.getInstructions()) {
					if ( instruction.getType().equals(InstructionType.ASSIGNMENT) ){
						AssignmentThread combinationalAssignment = new AssignmentThread(
								(Assignment) instruction, module.getAllVariables());
						threads.add(combinationalAssignment);
					}
					else if ( instruction.getType().equals(InstructionType.CODE_BLOCK) ){
						threads.add(getInstructionThreads((CodeBlock)instruction, module.getAllVariables()));
					}
				}
				combinationalThreads.put(module, threads);
			}
		}
		timer = new Timer();
	}

	private InstructionThread getInstructionThreads(CodeBlock codeBlock, Set<Variable> envVars) {
		ConditionalThread thread = new ConditionalThread(codeBlock.getCondition(), envVars);
		for ( Instruction instruction : codeBlock.getInstructions() ){
			if ( instruction.getType().equals(InstructionType.ASSIGNMENT) ){
				InstructionThread combinationalAssignment =
						new AssignmentThread((Assignment) instruction, envVars);
				thread.add(combinationalAssignment);
			}
			else if ( instruction.getType().equals(InstructionType.CODE_BLOCK) ){
				thread.add(getInstructionThreads((CodeBlock) instruction, envVars));
			}
		}
		return thread;
	}

	public void startSimulation() {
		for (ModuleDefinition module : combinationalThreads.keySet()) {
			for (InstructionThread instruction : combinationalThreads.get(module)) {
				timer.scheduleAtFixedRate(instruction, 500, (long) (10 + Math.random()));
			}
		}
	}

	public void stopSimulation() {
		timer.cancel();
	}

	public Map<ModuleDefinition, Set<Variable>> getModel() {
		return model;
	}

	public static void main(String[] args) throws ParsingException, InterruptedException {
		SystemVerilogSimulator simulator = SystemVerilogSimulator.getInstance();
		
		Scanner sc = new Scanner(System.in);
		sc.useDelimiter("finish");

		Set<String> modules = new HashSet<String>();
		while (sc.hasNext()) {
			String next = sc.next();
			if (next.trim().equals("00")) {
				break;
			}
			modules.add(next);
		}

		simulator.prepareSimulation(modules);
		simulator.startSimulation();
		
		Thread controller = new Thread(new SimulatorController(simulator));
		controller.start();
		
//		while (true) {
//
//			for ( ModuleDefinition module : simulator.getModel().keySet() ){
//				System.out.println("\nMODULE: "+module.getModuleName()+"\n");
//				for ( Variable var : module.getAllVariables() ){
//					System.out.format("\t%10s - %3d + (%s/%s)\n", var.getName(), var.getValue(), var.getMeaning(), var.getKind());
//				}
//			}
//			
//			Thread.sleep(10000);
//
//		}
	}

	private static synchronized SystemVerilogSimulator getInstance() {
		if ( systemVerilogSimulator == null ){
			systemVerilogSimulator = new SystemVerilogSimulator();
		}
		return systemVerilogSimulator;
	}

	public boolean setVariableValue(String varName, int varValue) {
		
		for ( ModuleDefinition module : SystemVerilogSimulator.getInstance().getModel().keySet() ){
			for ( Variable var : module.getInputVariables() ){
				if ( var.getName().equals(varName) ){
					var.setValue(varValue);
					return true;
				}
			}
		}
		return false;
	}

}
