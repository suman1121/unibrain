package dsc.controller;

public enum RegistrationType {NEW('N',"NEW"),RENEWAL('R',"RENEWAL");
	private char value;
	private String name;
	private RegistrationType(char value,String name) {
		
		this.value=value;
		this.name=name;
	} 
	
	public int getRegistrationType() {
        return value;
    }
	
	 
	public String getRegistrationTypeName() {
        return name;
    }
}
