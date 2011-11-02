package hardware_model;

import hardware_model.Condition;
import hardware_model.Instruction;
import hardware_model.LoopingCodeBlock;

import java.util.HashSet;
import java.util.Set;

public class Combinational implements LoopingCodeBlock {

	Set<Instruction> instructions;
	Condition condition;
	
	public Combinational(Condition loop){
		instructions = new HashSet<Instruction>();
		condition = loop;
	}
	
	public Combinational(){
		this(null);
	}
	
	@Override
	public Condition getLoopingCondition() {
		return condition;
	}

	@Override
	public Set<Instruction> getInstructions() {
		return instructions;
	}

	@Override
	public void addInstruction(Instruction instruction) {
		instructions.add(instruction);
	}

}
