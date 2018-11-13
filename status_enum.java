package dsc.controller;


public  enum status_enum { Submitted(10,"Submitted"), AmountAltered(15,"Amount Altered"), Initiated(20,"Initiated"), Paid(30,"Paid"),Issued(40,"Issued");
		private int value;  
		private String  name1;  
		private status_enum(int value,String name){  
		this.value=value;  
		this.name1=name;
		}  
		public int getStatus() {
	        return value;
	    }
		
		 
		public String getStatusName() {
	        return name1;
	    }
		} 	
	
