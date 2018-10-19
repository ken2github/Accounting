package test.on.data01;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import store.io.Normalizer;
import test.TestPath;

public class NormalizerTest {

	@Test
	public void test() {
		File directorySrc = new File(TestPath.BASE_PATH+"data-01\\2018 source");
		File directoryDest = new File(TestPath.BASE_PATH+"data-01\\2018");
		
		try {			
			Normalizer.directoryNormalize(directorySrc,directoryDest);
			assertEquals(14+2, directoryDest.listFiles().length);
			File[] files = directoryDest.listFiles();
			Arrays.sort(files);
			
			List<String> filenames = new ArrayList<>();
			for (File file : files) {
				filenames.add(file.getName());
			}
			
			assertEquals(true,filenames.contains("2018.01.BNP.6160.44.db"));
			assertEquals(true,filenames.contains("2018.02.BNP.6090.09.db"));
			assertEquals(true,filenames.contains("2018.03.BNP.8550.99.db"));
			assertEquals(true,filenames.contains("2018.04.BNP.8550.99.db"));
			assertEquals(true,filenames.contains("2018.05.BNP.8560.99.db"));
			assertEquals(true,filenames.contains("2018.06.BNP.8560.99.db"));
			
			assertEquals(true,filenames.contains("2018.01.BPN.991.38.db"));
			assertEquals(true,filenames.contains("2018.02.BPN.3902.95.db"));
			assertEquals(true,filenames.contains("2018.03.BPN.6398.02.db"));
			
			assertEquals(true,filenames.contains("2018.05.EDE.2361.99.db"));
			assertEquals(true,filenames.contains("2018.06.EDE.2442.55.db"));
			assertEquals(true,filenames.contains("2018.07.EDE.2627.80.db"));
			assertEquals(true,filenames.contains("2018.08.EDE.2808.80.db"));
			assertEquals(true,filenames.contains("2018.09.EDE.2254.38.db"));
			
		} catch (Exception e) {
			// TODO Auto.generated catch block
			e.printStackTrace();
			fail("Got unexpected exception: "+e.toString());
		}
	}	
	

}
