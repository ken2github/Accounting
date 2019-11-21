package restapi.bankfileconverter.service.reader.xls;

import java.math.BigDecimal;
import java.util.HashMap;

import restapi.bankfileconverter.service.reader.BankFileReader;

public class Spese_1_0_XLS_ReaderTests extends AbstractReaderTestTemplate {

	private static String fileName = "spese2019.xls";

	@Override
	public Config getTemplateConfig() {
		Config config = new Config();
		config.numberOfTransactions = 13;
		config.totalFlow = new BigDecimal("-315.00");
		config.dates = new HashMap<>();
		config.dates.put(0, "17/05/2019");
		config.dates.put(1, "06/05/2019");
		config.dates.put(12, "06/01/2019");

		config.dates.put(2, "04/05/2019");
		config.dates.put(3, "24/04/2019");
		config.dates.put(4, "26/02/2019");
		config.dates.put(5, "24/02/2019");
		config.dates.put(6, "12/02/2019");
		config.dates.put(7, "04/02/2019");
		config.dates.put(8, "30/01/2019");
		config.dates.put(9, "06/01/2019");
		config.dates.put(10, "06/01/2019");
		config.dates.put(11, "06/01/2019");

		return config;
	}

	@Override
	public BankFileReader getBankFileReader() {
		return new Spese_1_0_XLS_Reader(
				XLSHelper.getInputSteam_from_localFile(templateTestResourceBaseDirectory + fileName));
	}

}
