package deprecated.checking;

import java.util.ArrayList;
import java.util.List;

import deprecated.model.books.YearBook;

public abstract class AbstractExecutableRule implements CheckableRule {

	protected ExecutionStatus status = ExecutionStatus.PENDING;
	protected CheckType type = CheckType.NOTYPE;
	protected List<CheckException> exceptions = new ArrayList<>();
	
	@Override
	public ExecutionStatus getStatus() {
		return status;
	}

	@Override
	public ExecutionStatus execute(YearBook yb) throws Exception {
		//System.out.println(this.getClass().getName() +" "+status.name());
		if(status.equals(ExecutionStatus.PENDING)) {
			_execute(yb);
			if(status.equals(ExecutionStatus.PENDING)) {
				status = (exceptions.isEmpty())?ExecutionStatus.SUCCESS:ExecutionStatus.FAILED;
			}
			//System.out.println(this.getClass().getName() +" "+status.name());
		}
		return status;
	}

	@Override
	public ExecutionStatus skip() {
		if(status.equals(ExecutionStatus.PENDING)) {
			_skip();
			if(status.equals(ExecutionStatus.PENDING)) {
				status = ExecutionStatus.SKIPPED;
			}
		}
		return status;
	}

	@Override
	public CheckType getType() {
		return type;
	}

	@Override
	public List<CheckException> getExceptions() throws RuleStillInPendingStatus_Exception {
		if(status.equals(ExecutionStatus.PENDING)) {
			throw new RuleStillInPendingStatus_Exception();
		}
		return exceptions;
	}

	@Override
	public boolean isMandatory() {
		return type.isMandatory();
	}

	abstract protected void _skip();
	abstract protected void _execute(YearBook yb) throws Exception;
}
