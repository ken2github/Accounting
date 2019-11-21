package restapi.bankfileconverter.service.reader.xls;

import java.math.BigDecimal;
import java.util.HashMap;

import restapi.bankfileconverter.service.reader.BankFileReader;

public class BNP_1_0_XLS_ReaderTests extends AbstractReaderTestTemplate {

	private static String fileName = "count_bnp_from_22042018_to_21052019_balance_23624_45.xls";

	@Override
	public Config getTemplateConfig() {
		Config config = new Config();
		config.numberOfTransactions = 351;
		config.totalFlow = new BigDecimal("-4603.58");
		config.dates = new HashMap<>();
		config.dates.put(0, "20/05/2019");
		return config;
	}

	@Override
	public BankFileReader getBankFileReader() {
		return new BNP_1_0_XLS_Reader(
				XLSHelper.getInputSteam_from_localFile(templateTestResourceBaseDirectory + fileName));
	}

}
