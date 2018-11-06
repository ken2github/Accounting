package checking;

import model.books.CountMonthTransactions;
import model.books.MonthBook;
import model.books.YearBook;
import model.schema.Transaction;

public class CheckException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CheckException(CheckableRule crule, YearBook yb, MonthBook mb, CountMonthTransactions cmt, Transaction t, String error) {
		this(
			"In GROUP ["+(crule.getType().name())+"], the RULE ["+(crule.getClass().getName()) + "] is failed:"+"\n"+
			"Context:"+"\n"+
			" For"+
				((t==null)?"":" TRANSACTION ["+(t)+"], ")+ 
				((yb==null)?"":" YEAR ["+(yb.getYear())+"] ")+
				((mb==null)?"":" MONTH ["+(mb.getMonth())+"] ")+
				((cmt==null)?"":" COUNT ["+(cmt.getCount())+"] ")+"\n"+
			"Error:"+"\n"+
			" "+error
			);
	}
	
	private CheckException(String message) {
		super(message);
	}
	
}