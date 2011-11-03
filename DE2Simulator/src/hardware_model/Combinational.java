package hardware_model;

import java.util.HashSet;
import java.util.Set;

public class Combinational implements LoopingCodeBlock {

	Set<Assignment> instructions;
	Condition condition;
	
	public Combinational(Condition loop){
		instructions = new HashSet<Assignment>();
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
	public Set<Assignment> getInstructions() {
		return instructions;
	}

	@Override
	public void addInstruction(Assignment instruction) {
		instructions.add(instruction);
	}

}
