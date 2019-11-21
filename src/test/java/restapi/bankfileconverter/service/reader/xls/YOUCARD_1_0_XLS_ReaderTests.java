package restapi.bankfileconverter.service.reader.xls;

import java.math.BigDecimal;
import java.util.HashMap;

import restapi.bankfileconverter.service.reader.BankFileReader;

public class YOUCARD_1_0_XLS_ReaderTests extends AbstractReaderTestTemplate {

	private static String fileName = "count_youcard_from_24052018_to_24052019_balance_1130_66.xls";

	@Override
	public Config getTemplateConfig() {
		Config config = new Config();
		config.numberOfTransactions = 20;
		config.totalFlow = new BigDecimal("-1146.51");
		config.dates = new HashMap<>();
		config.dates.put(0, "09/05/2019");
		config.dates.put(4, "11/02/2019");
		return config;
	}

	@Override
	public BankFileReader getBankFileReader() {
		return new YOUCARD_1_0_XLS_Reader(
				XLSHelper.getInputSteam_from_localFile(templateTestResourceBaseDirectory + fileName));
	}

}
