package deprecated.checking;

/**
* Missing fields (sector, common, value, date, etc)
* Check month balance
* Missing month for count
* Missing count
* 
*/
public enum CheckType implements Comparable<CheckType>{

	NOTYPE(false),
	SINTAX(true), 
	COMPLETENESS(true), 
	FINANCE(true);
		
	public boolean isMandatory() {
		return mandatory;
	}

	private boolean mandatory;
	
	CheckType(boolean mandatory) {
		this.mandatory=mandatory;
	}
	
}
