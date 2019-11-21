package restapi.bankfileconverter.service.reader.xls.bank.reports.consistency;

import java.math.BigDecimal;
import java.util.HashMap;

import restapi.bankfileconverter.service.reader.BankFileReader;
import restapi.bankfileconverter.service.reader.xls.Edenred_1_0_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.XLSHelper;

public class EDENRED_ReportConsistencyTests_1 extends AbstractReportConsistenycTestTemplate {

	private static String fileName = "count_edenred_from_01012018_to_31122018_balance_X_X.xls";

	@Override
	public Config getTemplateConfig() {
		Config config = new Config();
		config.numberOfTransactions = 74;
		config.initialBalance = new BigDecimal("1229.07");
		config.finalBalance = new BigDecimal("2151.42");
		config.totalFlow = new BigDecimal("-2627.03");/// -2635.23
		config.dates = new HashMap<>();
		return config;
	}

	@Override
	public BankFileReader getBankFileReader() {
		return new Edenred_1_0_XLS_Reader(
				XLSHelper.getInputSteam_from_localFile(templateTestResourceBaseDirectory + fileName));
	}

}
