package simulator;

import hardware_model.Condition;
import hardware_model.Variable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConditionalThread extends InstructionThread {

	Set<InstructionThread> instructions;
	Condition condition;
	ExecutorService service;
	
	public ConditionalThread(Condition condition, Set<Variable> environmentVariables) {
		super(environmentVariables);
		this.condition = condition;
		this.instructions = new HashSet<InstructionThread>();
	}
	
	public void add(InstructionThread compositeInstruction){
		this.instructions.add(compositeInstruction);
	}

	@Override
	public void run() {
		this.service = Executors.newFixedThreadPool(instructions.size());
		synchronized(this){
			if ( condition == null ? true : testCondition(condition) ){
				for ( InstructionThread thread : instructions ){
					service.execute(thread);
				}
			}
		}
	}

	private boolean testCondition(Condition condition) {
		return calculateResult(condition) == condition.getExpectedValue();
	}

	private Object calculateResult(Condition condition) {
		return calculateResult(condition.getTest());
	}

}
