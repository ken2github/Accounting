package deprecated.checking;

import java.util.List;

import deprecated.model.books.YearBook;

public interface CheckableRule extends Executable<YearBook>{

	CheckType getType();
		
	List<CheckException> getExceptions() throws RuleStillInPendingStatus_Exception;
	
	boolean isMandatory();
	
}
