package checking.coherence;

/**
* Missing fields (sector, common, value, date, etc)
* Check month balance
* Missing month for count
* Missing count
* 
*/
public enum CheckType {

	SINTAX(0,true), 
	FINANCE(10,false);
	
	public int getPriority() {
		return priority;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	private int priority;
	private boolean mandatory;
	
	CheckType(int priority,boolean mandatory) {
		this.priority=priority;
		this.mandatory=mandatory;
	}
	
	
}
