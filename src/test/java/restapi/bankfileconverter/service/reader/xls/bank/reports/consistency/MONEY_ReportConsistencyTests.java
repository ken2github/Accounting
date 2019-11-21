package restapi.bankfileconverter.service.reader.xls.bank.reports.consistency;

import java.math.BigDecimal;
import java.util.HashMap;

import restapi.bankfileconverter.service.reader.BankFileReader;
import restapi.bankfileconverter.service.reader.xls.Spese_1_0_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.XLSHelper;

public class MONEY_ReportConsistencyTests extends AbstractReportConsistenycTestTemplate {

	private static String fileName = "count_money_from_01012018_to_06062019_balance_16975_00.xls";

	@Override
	public Config getTemplateConfig() {
		Config config = new Config();
		config.numberOfTransactions = 37;
		config.totalFlow = new BigDecimal("6048.86");
		config.initialBalance = new BigDecimal("6872.58");
		config.finalBalance = new BigDecimal("16975.00");
		config.dates = new HashMap<>();
		config.dates.put(0, "17/05/2019");
		config.dates.put(1, "06/05/2019");
		config.dates.put(35, "01/08/2018");
		config.dates.put(36, "29/06/2018");

		return config;
	}

	@Override
	public BankFileReader getBankFileReader() {
		return new Spese_1_0_XLS_Reader(
				XLSHelper.getInputSteam_from_localFile(templateTestResourceBaseDirectory + fileName));
	}

}
