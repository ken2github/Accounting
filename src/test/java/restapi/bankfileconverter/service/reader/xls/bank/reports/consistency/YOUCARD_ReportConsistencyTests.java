package restapi.bankfileconverter.service.reader.xls.bank.reports.consistency;

import java.math.BigDecimal;
import java.util.HashMap;

import restapi.bankfileconverter.service.reader.BankFileReader;
import restapi.bankfileconverter.service.reader.xls.XLSHelper;
import restapi.bankfileconverter.service.reader.xls.YOUCARD_1_0_XLS_Reader;

public class YOUCARD_ReportConsistencyTests extends AbstractReportConsistenycTestTemplate {

	private static String fileName = "count_youcard_from_01012018_to_24052019_balance_1130_66.xls";

	@Override
	public Config getTemplateConfig() {
		Config config = new Config();
		config.numberOfTransactions = 31;
		config.totalFlow = new BigDecimal("-1945.86");
		config.initialBalance = new BigDecimal("3076.52");
		config.finalBalance = new BigDecimal("1130.66");
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
