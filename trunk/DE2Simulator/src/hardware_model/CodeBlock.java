package hardware_model;

import java.util.HashSet;
import java.util.Set;

public class CodeBlock implements Instruction {

	Set<Instruction> instructions;
	Condition condition;
	
	public CodeBlock(Condition condition){
		this.instructions = new HashSet<Instruction>();
		this.condition = condition;
	}
	
	public CodeBlock(){
		this(null);
	}
	
	public Set<Instruction> getInstructions(){
		return instructions;
	}
	
	public void addInstruction(Instruction instruction){
		instructions.add(instruction);
	}
	
	public Condition getCondition(){
		return condition;
	}
	
	@Override
	public InstructionType getType(){
		return InstructionType.CODE_BLOCK;
	}
	
}
