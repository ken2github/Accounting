package deprecated.checking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.common.reflect.ClassPath;

import deprecated.model.books.YearBook;

@Service
public class CheckingEngine extends AbstractExecutableRule{
	
	//private ExecutionStatus status = ExecutionStatus.PENDING;
	private Map<CheckType,List<Executable<YearBook>>> rulePlan = new HashMap<>();
	//private Map<Class<? extends Executable<YearBook>>,Executable<YearBook>> instancesMap = new HashMap<>();
	private List<RuleGroup> phases = new ArrayList<>();
	
	public CheckingEngine() throws IOException, InstantiationException, IllegalAccessException {
		
		// Find all defined ChecRule implementations
		//System.out.println("Looking for ChecRules to enforce:");
		List<Executable<YearBook>> rules = new ArrayList<>();
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
		  if (info.getName().startsWith("checking.")) {
		    final Class<?> clazz = info.load();
		    if(Executable.class.isAssignableFrom(clazz) && 
		    		(!clazz.getName().equals(Executable.class.getName())) && 
		    		(!clazz.getName().equals(AbstractExecutableRule.class.getName())) && 
		    		(!clazz.getName().equals(CheckableRule.class.getName())) && 
		    		(!clazz.getName().equals(RuleGroup.class.getName())) && 
		    		(!clazz.getName().equals(CheckingEngine.class.getName()))) {
		    	//System.out.println(clazz.getName());
		    	
		    	@SuppressWarnings("unchecked")
				Class<? extends Executable<YearBook>> ruleClass = (Class<? extends Executable<YearBook>>)clazz;		   	
		    	Executable<YearBook> ruleInstance = ruleClass.newInstance();
		    	rules.add(ruleInstance);
		    	//instancesMap.put(ruleClass, ruleInstance);
		    }			    
		  }
		}
		//System.out.println("... found ["+rules.size()+"] rules.");
		
		// Build plan using Type and depndency info
		for (Executable<YearBook> rule : rules) {
			CheckType ct = rule.getType();
			if(!rulePlan.containsKey(ct)) {
				rulePlan.put(ct, new ArrayList<>());
			}
			rulePlan.get(ct).add(rule);			
		}
				
		// Init CheckPhases
		List<CheckType> sortedTypes = new ArrayList<>(rulePlan.keySet());
		sortedTypes.sort(null);
		for (CheckType ct : sortedTypes){
			phases.add(new RuleGroup(ct, rulePlan.get(ct)));
		}
				
	}
	
	protected void _execute(YearBook yb) throws Exception {
		boolean anyFailed = false;
		boolean anyMandatoryFailed = false;
		for (RuleGroup checkPhase : phases) {
			if(anyMandatoryFailed) {
				checkPhase.skip();
			}else{
				if(checkPhase.execute(yb).equals(ExecutionStatus.FAILED)) {
					anyFailed = true;
					anyMandatoryFailed = anyMandatoryFailed || checkPhase.isMandatory(); 
				}
			}
		}
		status = (anyFailed)?ExecutionStatus.FAILED:ExecutionStatus.SUCCESS;		
	}
	
	protected void _skip() {
		for (RuleGroup checkPhase : phases) {
			checkPhase.skip();
		}
	}
	
	public List<RuleGroup> getPhases(){
		return phases;
	}
	
}
