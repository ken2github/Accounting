package restapi.bankfileconverter.service.reader.xls.bank.reports.consistency;

import java.math.BigDecimal;
import java.util.HashMap;

import restapi.bankfileconverter.service.reader.BankFileReader;
import restapi.bankfileconverter.service.reader.xls.BNP_1_0_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.XLSHelper;

public class LA_ReportConsistencyTests extends AbstractReportConsistenycTestTemplate {

	private static String fileName = "count_la_from_01012018_to_21052019_balance 23067_73.xls";

	@Override
	public Config getTemplateConfig() {
		Config config = new Config();
		config.numberOfTransactions = 1;
		config.initialBalance = new BigDecimal("22896.01");
		config.finalBalance = new BigDecimal("23067.73");
		config.totalFlow = new BigDecimal("171.72");
		config.dates = new HashMap<>();
		config.dates.put(0, "31/12/2018");
		return config;
	}

	@Override
	public BankFileReader getBankFileReader() {
		return new BNP_1_0_XLS_Reader(
				XLSHelper.getInputSteam_from_localFile(templateTestResourceBaseDirectory + fileName));
	}

}
