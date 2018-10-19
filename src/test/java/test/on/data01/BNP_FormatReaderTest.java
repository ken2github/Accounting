package test.on.data01;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import java.io.File;
import store.io.FormatMapper;
import store.io.FormatReader;
import store.io.readers.Parametric_FormatReader;
import test.TestPath;

public class BNP_FormatReaderTest {

	@Test
	public void test() {
		FormatReader r = new Parametric_FormatReader(FormatMapper.BNP_FORMAT);
		try {
			r.loadSourceFile(new File(TestPath.BASE_PATH+"data-01\\2018 source\\bnp.2018.01.02.balance.6090.09.csv"));
			assertTrue(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Got unexpected exception: "+e.toString());
		}
		
		
	}

}
