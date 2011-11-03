package hardware_model;

import java.util.Set;

public interface CodeBlock {

	public Set<Assignment> getInstructions();
	public void addInstruction(Assignment instruction);
	
}
