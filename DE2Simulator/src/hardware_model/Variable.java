package hardware_model;

public class Variable {

	String meaning;
	String kind;
	int size;
	String name;
	int amount;
	
	public Variable(String meaning, String kind, int size,
			String name, int amount) {
		this.meaning = meaning;
		this.kind = kind;
		this.size = size;
		this.name = name;
		this.amount = amount;
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

}
