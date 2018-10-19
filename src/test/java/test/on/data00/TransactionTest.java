package test.on.data00;

import java.sql.Date;
import java.text.ParseException;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

import model.schema.Transaction;

import static org.junit.Assert.assertEquals;

public class TransactionTest {

	@Test
	public void test() {
		
		try {
			Transaction t = new Transaction(new String[]{"2018-01-23","1","0","spesa carrefour","sps.cibo","y"});
			myAsserts(t);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Got unexpected exception: "+e.toString());
		}		
	}
	
	@SuppressWarnings("deprecation")
	public static void myAsserts(Transaction t) {
		assertEquals(1.0D,t.getAmount(),0.001d);
		assertEquals("spesa carrefour",t.getTitle());
		assertTrue(t.isCommon());
		assertEquals("sps.cibo",t.getSector());
		assertEquals(new Date(118,0,23),t.getDate());
	}

}
