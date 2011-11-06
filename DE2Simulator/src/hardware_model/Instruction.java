package hardware_model;

public interface Instruction {

	public enum InstructionType {
		ASSIGNMENT, CODE_BLOCK;
	}
	
	public InstructionType getType();
	
}
