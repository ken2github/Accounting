package restapi.bankprovisioninghelper.service.analyser;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import model2.DetailedTransaction;
import restapi.bankprovisioninghelper.api.Relevance;

public class BankTransactionAnalyserTests {

	private static BankTransactionAnalyser bta;
	private static List<DetailedTransaction> transactions;

	@BeforeClass
	public static void init() {
		bta = new BankTransactionAnalyser(new BasicTokenizer(), new TFIDFCalculator());
		transactions = new ArrayList<DetailedTransaction>();

		transactions.add((DetailedTransaction) new DetailedTransaction().setId("1")
				.setTitle("pagamento internet - carta*5842-12:21-mgp*leetchi.com0355938 paris fra")
				.setSectorName("REG"));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId("2")
				.setTitle("PAIEMENT CB CARREFOUR (FRANCE) DU 25/10").setSectorName("SPS"));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId("3")
				.setTitle("PAIEMENT CB SATORIZ VALLAUR DU 23/10 A VALLAURIS").setSectorName("SPS.CIBO"));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId("4")
				.setTitle("PAIEMENT CB CARREFOUR (FRANCE) DU 18/10").setSectorName("SPS"));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId("5")
				.setTitle("CARREFOUR TPE - CHEMIN DE SAINT CLAUDE  ANTIBES 06600").setSectorName("SPS"));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId("6")
				.setTitle("TABLE DES INDES ANTIBES, Commerçant non autorisé").setSectorName("SPS.RISTO"));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId("7")
				.setTitle("SODEXO FR600881 - 790 AV DU DOCTEUR MAURICE DONAT  MOUGINS 06250")
				.setSectorName("SPS.CIBO"));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId("8")
				.setTitle("pagamento internet - carta*5842-21:37-easyjet000ewmr2l4 luton, beds gbr")
				.setSectorName("VAR.EXTRA"));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId("9")
				.setTitle("pagamento internet - carta*5842-00:00-amzn mktp fr amazon.fr lux").setSectorName("OGG"));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId("10")
				.setTitle("pagamento internet - carta*5842-12:21-mgp*leetchi.com0355938 paris fra")
				.setSectorName("REG"));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId("11")
				.setTitle("pagamento internet - carta*5842-15:36-mgp*leetchi.com0355938 paris fra")
				.setSectorName("REG"));

		bta.refreshTokensMap(transactions);
	}

	@Test
	public void suggestREGOk() {
		List<String> sectors = bta.suggestSectorsForTransaction(new DetailedTransaction().setId("1")
				.setTitle("pagamento internet - carta*5842-12:21-mgp*leetchi.com0355938 paris fra")
				.setSectorName("REG"), 3, Relevance.HIGH);
		sectors.stream().forEach(s -> System.out.println("[" + s + "]"));
		assertEquals("REG", sectors.get(0));
	}

	@Test
	public void suggestSPSOk() {
		List<String> sectors = bta.suggestSectorsForTransaction(new DetailedTransaction().setId("1")
				.setTitle("PAIEMENT CB CARREFOUR (FRANCE) DU 25/10").setSectorName("REG"), 3, Relevance.HIGH);
		sectors.stream().forEach(s -> System.out.println("[" + s + "]"));
		assertEquals("SPS", sectors.get(0));
	}

}
