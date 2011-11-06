package hardware_model;


public class Condition {

	Variable test;
	int expectedValue;
	
	public Condition(Variable variable, int expectedValue){
		this.test = variable;
		this.expectedValue = expectedValue;
	}
	
	public boolean isTrue(){
		return test.getValue() == expectedValue;
	}

}
