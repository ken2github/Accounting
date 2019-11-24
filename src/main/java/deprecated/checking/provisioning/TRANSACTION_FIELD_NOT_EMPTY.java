package deprecated.checking.provisioning;
//package checking.provisioning;
//
//import checking.coherence.CheckRule;
//import checking.coherence.CheckType;
//import model.books.CountMonthTransactions;
//import model.books.MonthBook;
//import model.books.YearBook;
//import model.schema.Transaction;
//
//public class TRANSACTION_FIELD_NOT_EMPTY implements CheckRule {
//
//	@Override
//	public CheckType getType() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean apply(YearBook yb) {
//		for (MonthBook mb : yb.getMonthBooks()) {
//			for (CountMonthTransactions cm : mb.getListOfcountMonthTransactions()) {
//				for (Transaction t : cm.getTransactions()) {
//					t.getAmount();
//					t.getDate();
//					t.getSector();
//					t.getTitle();
//					t.isCommon();
//				}
//			}
//		}
//		return false;
//	}
//
//}
