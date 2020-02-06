package test.on.velocity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import deprecated.velocity.VelocityReporter;

public class VelocityTest {

	@Test
	public void test() {
		VelocityReporter vr = new VelocityReporter();
		try {
			File outputDirectpry = new File("C:\\Users\\primo\\git\\Accounting\\src\\test\\resources\\velocity");
			vr.createReport(outputDirectpry);
			assertEquals(1, outputDirectpry.listFiles().length);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Got unexpected exception: " + e.toString());
		}

	}

}
