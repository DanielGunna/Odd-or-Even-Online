package labreso.model;


public enum MessageEnum {
	PEDRA("Pedra"),
	PAPEL("Papel"),
	TESOURA("Tesoura");
	
	
	private final String value;
	
	private MessageEnum(String values) {
		this.value = values;
	}

	public String getValue() {
		return value;
	}
}
