package restapi.bankfileconverter.service.reader.xls.bank.reports.consistency;

import java.math.BigDecimal;
import java.util.HashMap;

import restapi.bankfileconverter.service.reader.BankFileReader;
import restapi.bankfileconverter.service.reader.xls.Edenred_1_0_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.XLSHelper;

public class EDENRED_ReportConsistencyTests_2 extends AbstractReportConsistenycTestTemplate {

	private static String fileName = "count_edenred_from_01012019_to_21052019_balance 2151_42.xls";

	@Override
	public Config getTemplateConfig() {
		Config config = new Config();
		config.numberOfTransactions = 70;
		config.initialBalance = new BigDecimal("1511.05");
		config.finalBalance = new BigDecimal("1229.07");
		config.totalFlow = new BigDecimal("-281.98");/// -2635.23
		config.dates = new HashMap<>();
		return config;
	}

	@Override
	public BankFileReader getBankFileReader() {
		return new Edenred_1_0_XLS_Reader(
				XLSHelper.getInputSteam_from_localFile(templateTestResourceBaseDirectory + fileName));
	}

}
