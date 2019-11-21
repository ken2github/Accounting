package restapi.bankfileconverter.service.reader.xls.bank.reports.consistency;

import java.math.BigDecimal;
import java.util.HashMap;

import restapi.bankfileconverter.service.reader.BankFileReader;
import restapi.bankfileconverter.service.reader.xls.BNP_1_0_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.XLSHelper;

public class PEL_ReportConsistencyTests extends AbstractReportConsistenycTestTemplate {

	private static String fileName = "count_pel_from_01012018_to_21052019_balance 63146_74.xls";

	@Override
	public Config getTemplateConfig() {
		Config config = new Config();
		config.numberOfTransactions = 19;
		config.initialBalance = new BigDecimal("61315.75");
		config.finalBalance = new BigDecimal("63146.74");
		config.totalFlow = new BigDecimal("1830.99");
		config.dates = new HashMap<>();
		config.dates.put(0, "30/04/2019");
		return config;
	}

	@Override
	public BankFileReader getBankFileReader() {
		return new BNP_1_0_XLS_Reader(
				XLSHelper.getInputSteam_from_localFile(templateTestResourceBaseDirectory + fileName));
	}

}
