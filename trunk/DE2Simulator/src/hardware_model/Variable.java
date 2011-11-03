package hardware_model;

public class Variable {

	String meaning;
	String kind;
	int size;
	String name;
	int amount;
	int value;
	
	public Variable(String meaning, String kind, int size,
			String name, int amount) {
		this.meaning = meaning;
		this.kind = kind;
		this.size = size;
		this.name = name;
		this.amount = amount;
		this.value = 0;
	}

	public String getMeaning() {
		return meaning;
	}

	public String getKind() {
		return kind;
	}

	public int getSize() {
		return size;
	}

	public int getAmount() {
		return amount;
	}

	public String getName() {
		return name;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value){
		this.value = value;
	}
	
	@Override
	public String toString(){
		return name;
	}

}
