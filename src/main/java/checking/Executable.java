package checking;

import java.util.List;

public interface Executable<T> {
			
	CheckType getType();
	
	ExecutionStatus getStatus();
	
	ExecutionStatus execute(T t) throws Exception;
	
	ExecutionStatus skip();

	List<CheckException> getExceptions() throws RuleStillInPendingStatus_Exception;
	
	boolean isMandatory();
	
}
