package deprecated.checking.coherence;

import deprecated.checking.AbstractExecutableRule;
import deprecated.checking.CheckException;
import deprecated.checking.CheckType;
import deprecated.model.books.CountMonthTransactions;
import deprecated.model.books.MonthBook;
import deprecated.model.books.YearBook;
import deprecated.model.schema.Transaction;

public class ValidSectorFieldInTransaction extends AbstractExecutableRule {
		
	public ValidSectorFieldInTransaction() {
		type = CheckType.SINTAX;
	}
	
	protected void _execute(YearBook yb) throws Exception{
		for (MonthBook mb : yb.getMonthBooks()) {
			for (CountMonthTransactions cmt : mb.getListOfcountMonthTransactions()) {
				for (Transaction t : cmt.getTransactions()) {
					if((t.getSector()==null) || (t.getSector().equals(""))){
						exceptions.add(new CheckException(this,yb,mb,cmt,t,"SECTOR field is empty or null"));
						
					} else {
						if(!yb.getSchema().getSectors().contains(t.getSector())) {
							exceptions.add(new CheckException(this,yb,mb,cmt,t,"SECTOR ["+(t.getSector())+"] field is not listed in SCHEMA"));
						}
					} 
				}
			}
		}
	}

	protected void _skip() {}

}
