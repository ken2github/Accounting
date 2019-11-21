package restapi.bankfileconverter.service.reader.xls.bank.reports.consistency;

import java.math.BigDecimal;
import java.util.HashMap;

import restapi.bankfileconverter.service.reader.BankFileReader;
import restapi.bankfileconverter.service.reader.xls.BNP_1_0_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.XLSHelper;

public class LDD_ReportConsistencyTests extends AbstractReportConsistenycTestTemplate {

	private static String fileName = "count_ldd_from_01012018_to_21052019_balance 12158_32.xls";

	@Override
	public Config getTemplateConfig() {
		Config config = new Config();
		config.numberOfTransactions = 1;
		config.initialBalance = new BigDecimal("12067.81");
		config.finalBalance = new BigDecimal("12158.32");
		config.totalFlow = new BigDecimal("90.51");
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
