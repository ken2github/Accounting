package restapi.bankfileconverter.service.reader.xls;

import java.math.BigDecimal;
import java.util.HashMap;

import restapi.bankfileconverter.service.reader.BankFileReader;

public class BPN_1_0_XLS_ReaderTests extends AbstractReaderTestTemplate {

	private static String fileName = "count_bpn_from_24052018_to_24052019_balance_24350_72.xls";

	@Override
	public Config getTemplateConfig() {
		Config config = new Config();
		config.numberOfTransactions = 14;
		config.totalFlow = new BigDecimal("9916.08");
		config.dates = new HashMap<>();
		config.dates.put(0, "03/04/2019");
		config.dates.put(5, "17/12/2018");
		return config;
	}

	@Override
	public BankFileReader getBankFileReader() {
		return new BPN_1_0_XLS_Reader(
				XLSHelper.getInputSteam_from_localFile(templateTestResourceBaseDirectory + fileName));
	}

}
