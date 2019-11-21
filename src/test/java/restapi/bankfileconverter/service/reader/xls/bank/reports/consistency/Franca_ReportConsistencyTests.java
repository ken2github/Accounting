package restapi.bankfileconverter.service.reader.xls.bank.reports.consistency;

import java.math.BigDecimal;
import java.util.HashMap;

import restapi.bankfileconverter.service.reader.BankFileReader;
import restapi.bankfileconverter.service.reader.xls.Internal_1_0_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.XLSHelper;

public class Franca_ReportConsistencyTests extends AbstractReportConsistenycTestTemplate {

	private static String fileName = "count_franca_from_01012018_to_31122019_balance_X_X.xls";

	@Override
	public Config getTemplateConfig() {
		Config config = new Config();
		config.numberOfTransactions = 0;
		config.totalFlow = new BigDecimal("0.00");
		config.initialBalance = new BigDecimal("-10503.67");
		config.finalBalance = new BigDecimal("-10503.67");
		config.dates = new HashMap<>();
		// config.dates.put(0, "01/03/2019");
		return config;
	}

	@Override
	public BankFileReader getBankFileReader() {
		return new Internal_1_0_XLS_Reader(
				XLSHelper.getInputSteam_from_localFile(templateTestResourceBaseDirectory + fileName));
	}

}
