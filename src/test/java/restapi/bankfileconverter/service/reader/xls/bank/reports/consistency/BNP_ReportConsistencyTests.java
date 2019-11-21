package restapi.bankfileconverter.service.reader.xls.bank.reports.consistency;

import java.math.BigDecimal;
import java.util.HashMap;

import org.junit.Test;

import restapi.bankfileconverter.service.reader.BankFileReader;
import restapi.bankfileconverter.service.reader.xls.BNP_1_0_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.XLSHelper;

public class BNP_ReportConsistencyTests extends AbstractReportConsistenycTestTemplate {

	private static String fileName = "count_bnp_from_01012018_to_21052019_balance_23624_45.xls";

	@Override
	public Config getTemplateConfig() {
		Config config = new Config();
		config.numberOfTransactions = 435;
		config.initialBalance = new BigDecimal("31159.18");
		config.finalBalance = new BigDecimal("23624.45");
		config.totalFlow = new BigDecimal("-7534.73");
		config.dates = new HashMap<>();
		config.dates.put(0, "20/05/2019");
		return config;
	}

	@Override
	public BankFileReader getBankFileReader() {
		return new BNP_1_0_XLS_Reader(
				XLSHelper.getInputSteam_from_localFile(templateTestResourceBaseDirectory + fileName));
	}

	@Test
	public void check() {
		System.out.println(this.transactions.get(this.transactions.size() - 1).getAmount());
	}

}
