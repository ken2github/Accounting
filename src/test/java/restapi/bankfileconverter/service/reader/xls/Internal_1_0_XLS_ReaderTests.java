package restapi.bankfileconverter.service.reader.xls;

import java.math.BigDecimal;
import java.util.HashMap;

import restapi.bankfileconverter.service.reader.BankFileReader;

public class Internal_1_0_XLS_ReaderTests extends AbstractReaderTestTemplate {

	private static String fileName = "internal_1_0.xls";

	@Override
	public Config getTemplateConfig() {
		Config config = new Config();
		config.numberOfTransactions = 1;
		config.totalFlow = new BigDecimal("1250.00");
		config.dates = new HashMap<>();
		config.dates.put(0, "01/03/2019");
		return config;
	}

	@Override
	public BankFileReader getBankFileReader() {
		return new Internal_1_0_XLS_Reader(
				XLSHelper.getInputSteam_from_localFile(templateTestResourceBaseDirectory + fileName));
	}

}
