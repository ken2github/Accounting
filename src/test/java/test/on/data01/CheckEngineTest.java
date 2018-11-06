package test.on.data01;

import static org.junit.Assert.*;

import java.io.File;
import org.junit.Test;

import checking.CheckingEngine;
import checking.CheckException;
import checking.RuleGroup;
import checking.ExecutionStatus;
import model.books.YearBook;
import test.TestPath;

public class CheckEngineTest {

	@Test
	public void test() {
		try {
			YearBook yb = new YearBook(new File(TestPath.BASE_PATH+"data-01\\2018"));
			
			CheckingEngine ce = new CheckingEngine();
			ExecutionStatus status = ce.execute(yb);
			if(status.equals(ExecutionStatus.FAILED)) {
				for (RuleGroup cp : ce.getPhases()) {
					for (CheckException cpe : cp.getExceptions()) {
						System.out.println(cpe.getMessage());
					}
				}
			}
			assertEquals(ExecutionStatus.SUCCESS, status);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Got unexpected exception: "+e.toString());
		} 
	}

}
