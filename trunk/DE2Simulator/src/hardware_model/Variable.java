package hardware_model;

public class Variable {

	String meaning;
	String kind;
	String name;
	Value value;
	
	public Variable(String meaning, String kind, int size,
			String name, int amount) {
		this.meaning = meaning;
		this.kind = kind;
		this.name = name;
		this.value = new Value(size, amount);
	}

	public String getMeaning() {
		return meaning;
	}

	public String getKind() {
		return kind;
	}

	public int getSize() {
		return value.getSize();
	}

	public int getAmount() {
		return value.getAmount();
	}

	public String getName() {
		return name;
	}
	
	public int getValue() {
		return value.getValue();
	}
	
	public synchronized void setValue(int value){
		this.value.setValue(value);
	}
	
	@Override
	public String toString(){
		return name;
	}

}
