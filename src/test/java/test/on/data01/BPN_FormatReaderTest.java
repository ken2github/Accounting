package test.on.data01;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

import deprecated.store.io.FormatMapper;
import deprecated.store.io.FormatReader;
import deprecated.store.io.TransactionFilterException;
import deprectaed.store.io.readers.Parametric_FormatReader;

import java.io.File;

import test.TestPath;

public class BPN_FormatReaderTest {

	@Test
	public void test() {
		FormatReader r = new Parametric_FormatReader(FormatMapper.BPN_FORMAT,new TransactionFilterException() {});
		try {
			r.loadSourceFile(new File(TestPath.BASE_PATH+"data-01\\2018 source\\bpn.2018.01.02.03.balance.6381.18.csv"));
			assertTrue(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Got unexpected exception: "+e.toString());
		}
		
		
	}

}
