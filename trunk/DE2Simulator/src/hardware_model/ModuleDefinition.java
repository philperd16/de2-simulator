package hardware_model;


import java.util.HashSet;
import java.util.Set;

public class ModuleDefinition {

	String moduleName;
	Set<Variable> inputVariables;
	Set<Variable> outputVariables;
	Set<Variable> localVariables;
	Set<CodeBlock> combinationalBlocks;
	Set<CodeBlock> sequentialBlocks;
	
	public ModuleDefinition(String moduleName) {
		this.moduleName = moduleName;
		this.inputVariables = new HashSet<Variable>();
		this.outputVariables = new HashSet<Variable>();
		this.localVariables = new HashSet<Variable>();
		this.combinationalBlocks = new HashSet<CodeBlock>();
		this.sequentialBlocks = new HashSet<CodeBlock>();
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

	public Set<CodeBlock> getCombinationalBlocks() {
		return combinationalBlocks;
	}

	public Set<CodeBlock> getSequentialBlocks() {
		return sequentialBlocks;
	}

	public Set<Variable> getAllVariables() {
		Set<Variable> all = new HashSet<Variable>(localVariables);
		all.addAll(inputVariables);
		all.addAll(outputVariables);
		return all;
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

	public void addCombinationalBlock(CodeBlock combinationalBlock) {
		combinationalBlocks.add(combinationalBlock);
	}

}
