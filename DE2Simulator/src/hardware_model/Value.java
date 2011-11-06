package hardware_model;

public class Value {

	private int size;
	private int amount;
	long[] registers;
	
	public Value(int size, int amount) {
		this.size = size;
		this.amount = amount;
		this.registers = new long[amount];
	}

	public int getSize() {
		return size;
	}

	public int getAmount() {
		return amount;
	}
	
	public void setValue(int value, int address){
		this.registers[address] = value % (1 << size);
		if ( value < 0 ){
			this.registers[address] = this.registers[address] + (1 << size);
		}
	}
	
	public void setValue(int value){
		this.setValue(value, 0);
	}
	
	public int getValue(int address){
		return (int) registers[address];
	}
	
	public int getValue(){
		return this.getValue(0);
	}
	
}
