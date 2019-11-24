package deprecated.checking;

import java.util.ArrayList;
import java.util.List;

import deprecated.model.books.YearBook;

public class RuleGroup extends AbstractExecutableRule {
	
	private List<Executable<YearBook>> rules = new ArrayList<>();
	
	public RuleGroup(CheckType checkType,List<Executable<YearBook>> rules) {
		this.type=checkType;
		this.rules.addAll(rules);
	}	

	protected void _execute(YearBook yb) throws Exception {		
		//boolean anyFailed = false;
		for (Executable<YearBook> rule : rules) {
			if(rule.execute(yb).equals(ExecutionStatus.FAILED)) {
				exceptions.addAll(rule.getExceptions());
			}
			//	anyFailed = anyFailed || (ruleStatus.equals(ExecutionStatus.FAILED));
		}
		//System.out.println(type.name() + " "+ exceptions.size() + " "+status.name());
		//status = (anyFailed)? ExecutionStatus.FAILED:ExecutionStatus.SUCCESS;		
	}
	
	protected void _skip() {
		for (Executable<YearBook> cr : rules) {
			cr.skip();
		}		
	}
	
}
