package hardware_model;

import hardware_model.Sequential;

import java.util.HashSet;
import java.util.Set;

public class ModuleDefinition {

	String moduleName;
	Set<Variable> inputVariables;
	Set<Variable> outputVariables;
	Set<Variable> localVariables;
	Set<Combinational> combinationalBlocks;
	Set<Sequential> sequentialBlocks;
	
	public ModuleDefinition(String moduleName) {
		this.moduleName = moduleName;
		this.inputVariables = new HashSet<Variable>();
		this.outputVariables = new HashSet<Variable>();
		this.localVariables = new HashSet<Variable>();
		this.combinationalBlocks = new HashSet<Combinational>();
		this.sequentialBlocks = new HashSet<Sequential>();
	}

	public String getModuleName() {
		return moduleName;
	}

	public Set<Variable> getInputVariables() {
		return inputVariables;
	}

	public Set<Variable> getOutputVariables() {
		return outputVariables;
	}

	public Set<Variable> getLocalVariables() {
		return localVariables;
	}

	public Set<Variable> getConstants() {
		return null;
	}

	public Set<Combinational> getCombinationalBlocks() {
		return combinationalBlocks;
	}

	public Set<Sequential> getSequentialBlocks() {
		return sequentialBlocks;
	}

	public void addInputVariable(String meaning, String kind, int size,
			String name, int amount) {
		inputVariables.add(new Variable(meaning, kind, size, name, amount));
	}

	public void addOutputVariable(String meaning, String kind, int size,
			String name, int amount) {
		outputVariables.add(new Variable(meaning, kind, size, name, amount));
	}

	public void addLocalVariable(String kind, int size, String name, int amount) {
		localVariables.add(new Variable("local", kind, size, name, amount));
	}

	public void addCombinationalBlock(Combinational combinationalBlock) {
		combinationalBlocks.add(combinationalBlock);
	}

}
