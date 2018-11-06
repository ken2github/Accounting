package test.on.velocity;

import static org.junit.Assert.*;
import java.io.File;
import org.junit.Test;

import reporting.ReportException;
import reporting.Reportable;
import reporting.VelocityReporter;

public class VelocityTest {

	@Test
	public void test() {
		Reportable vr = new VelocityReporter();
		try {
			File outputDirectpry = new File("C:\\Users\\primo\\git\\Accounting\\src\\test\\resources\\velocity"); 
			vr.createReport(null, outputDirectpry);
			assertEquals(1, outputDirectpry.listFiles().length);
		} catch (ReportException e) {
			e.printStackTrace();
			fail("Got unexpected exception: "+e.toString());
		}
		
	}

}
