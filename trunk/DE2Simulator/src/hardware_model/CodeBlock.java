package hardware_model;

import java.util.Set;

public interface CodeBlock {

	public Set<Instruction> getInstructions();
	public void addInstruction(Instruction instruction);
	
}
