package restapi.bankfileconverter.service.reader.xls;

import java.math.BigDecimal;
import java.util.HashMap;

import restapi.bankfileconverter.service.reader.BankFileReader;

public class Edenred_1_0_XLS_ReaderTests extends AbstractReaderTestTemplate {

	private static String fileName = "count_edenred_from_01012019_to_21052019_balance 2151_42.xls";

	@Override
	public Config getTemplateConfig() {
		Config config = new Config();
		config.numberOfTransactions = 70;
		config.totalFlow = new BigDecimal("-281.98");
		config.dates = new HashMap<>();
		config.dates.put(0, "18/05/2019");
		config.dates.put(2, "17/05/2019");
		config.dates.put(66, "30/01/2019");
		config.dates.put(68, "11/01/2019");
		config.dates.put(69, "09/01/2019");

		config.dates.put(1, "17/05/2019");
		config.dates.put(5, "15/05/2019");
		config.dates.put(14, "23/04/2019");
		config.dates.put(31, "19/03/2019");
		config.dates.put(41, "01/03/2019");
		config.dates.put(52, "01/03/2019");
		config.dates.put(55, "27/02/2019");

		return config;
	}

	@Override
	public BankFileReader getBankFileReader() {
		return new Edenred_1_0_XLS_Reader(
				XLSHelper.getInputSteam_from_localFile(templateTestResourceBaseDirectory + fileName));
	}

}
