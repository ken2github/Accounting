package checking.coherence;

import checking.AbstractExecutableRule;
import checking.CheckException;
import checking.CheckType;
import model.books.CountMonthTransactions;
import model.books.MonthBook;
import model.books.YearBook;
import model.schema.Transaction;

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
