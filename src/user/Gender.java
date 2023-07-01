package user;

public enum Gender {
	
	MALE("male", 'm'), 
	FEMALE("female", 'f'), 
	OTHER("other", 'o');
	
	private String name;
	private char symbol;

	private Gender(String name, char symbol) {
		this.name = name;
		this.symbol = symbol;
	}
	
	public String getName() {
		return name;
	}
	
	public char getSymbol() {
		return symbol;
	}
	
	@Override
	public String toString() { 
		return "lesson6.Gender[name=" + name + ", symbol=" + symbol + "]";
	}

}
