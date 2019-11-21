package restapi.bankfileconverter.service.reader.xls.bank.reports.consistency;

import java.math.BigDecimal;
import java.util.HashMap;

import restapi.bankfileconverter.service.reader.BankFileReader;
import restapi.bankfileconverter.service.reader.xls.BPN_1_0_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.XLSHelper;

public class BPN_ReportConsistencyTests extends AbstractReportConsistenycTestTemplate {

	private static String fileName = "count_bpn_from_01012018_to_24052019_balance_24350_72.xls";

	@Override
	public Config getTemplateConfig() {
		Config config = new Config();
		config.numberOfTransactions = 19;
		config.totalFlow = new BigDecimal("12394.70");
		config.initialBalance = new BigDecimal("11956.02");
		config.finalBalance = new BigDecimal("24350.72");
		config.dates = new HashMap<>();
		config.dates.put(0, "09/01/2018");
		config.dates.put(5, "30/03/2019");
		return config;
	}

	@Override
	public BankFileReader getBankFileReader() {
		return new BPN_1_0_XLS_Reader(
				XLSHelper.getInputSteam_from_localFile(templateTestResourceBaseDirectory + fileName));
	}

}
