package hardware_model;

import hardware_model.operation.Operation;


public class Condition {

	Operation test;
	int expectedValue;
	
	public Condition(Operation test, int expectedValue){
		this.test = test;
		this.expectedValue = expectedValue;
	}

	public Object getExpectedValue() {
		return expectedValue;
	}

	public Operation getTest() {
		return test;
	}
	
}
