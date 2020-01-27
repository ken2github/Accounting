package restapi.bankprovisioninghelper.service.analyser;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import model2.DetailedTransaction;
import restapi.transactionsoracle.api.SimilarityRelevance;
import restapi.transactionsoracle.service.analyser.BankTransactionAnalyser;
import restapi.transactionsoracle.service.analyser.BasicTokenizer;
import restapi.transactionsoracle.service.analyser.TFIDFCalculator;

public class BankTransactionAnalyserTests {

	private static BankTransactionAnalyser bta;
	private static List<DetailedTransaction> transactions;
	private static List<String> sectors;

	@BeforeClass
	public static void init() {
		bta = new BankTransactionAnalyser(new BasicTokenizer(), new TFIDFCalculator());
		transactions = new ArrayList<DetailedTransaction>();

		// transactions.add((DetailedTransaction) new DetailedTransaction().setId("1")
		// .setTitle("pagamento internet - carta*5842-12:21-mgp*leetchi.com0355938 paris
		// fra")
		// .setSectorName("REG"));
		// transactions.add((DetailedTransaction) new DetailedTransaction().setId("2")
		// .setTitle("PAIEMENT CB CARREFOUR (FRANCE) DU 25/10").setSectorName("SPS"));
		// transactions.add((DetailedTransaction) new DetailedTransaction().setId("3")
		// .setTitle("PAIEMENT CB SATORIZ VALLAUR DU 23/10 A
		// VALLAURIS").setSectorName("SPS.CIBO"));
		// transactions.add((DetailedTransaction) new DetailedTransaction().setId("4")
		// .setTitle("PAIEMENT CB CARREFOUR (FRANCE) DU 18/10").setSectorName("SPS"));
		// transactions.add((DetailedTransaction) new DetailedTransaction().setId("5")
		// .setTitle("CARREFOUR TPE - CHEMIN DE SAINT CLAUDE ANTIBES
		// 06600").setSectorName("SPS"));
		// transactions.add((DetailedTransaction) new DetailedTransaction().setId("6")
		// .setTitle("TABLE DES INDES ANTIBES, Commerçant non
		// autorisé").setSectorName("SPS.RISTO"));
		// transactions.add((DetailedTransaction) new DetailedTransaction().setId("7")
		// .setTitle("SODEXO FR600881 - 790 AV DU DOCTEUR MAURICE DONAT MOUGINS 06250")
		// .setSectorName("SPS.CIBO"));
		// transactions.add((DetailedTransaction) new DetailedTransaction().setId("8")
		// .setTitle("pagamento internet - carta*5842-21:37-easyjet000ewmr2l4 luton,
		// beds gbr")
		// .setSectorName("VAR.EXTRA"));
		// transactions.add((DetailedTransaction) new DetailedTransaction().setId("9")
		// .setTitle("pagamento internet - carta*5842-00:00-amzn mktp fr amazon.fr
		// lux").setSectorName("OGG"));
		// transactions.add((DetailedTransaction) new DetailedTransaction().setId("10")
		// .setTitle("pagamento internet - carta*5842-12:21-mgp*leetchi.com0355938 paris
		// fra")
		// .setSectorName("REG"));
		// transactions.add((DetailedTransaction) new DetailedTransaction().setId("11")
		// .setTitle("pagamento internet - carta*5842-15:36-mgp*leetchi.com0355938 paris
		// fra")
		// .setSectorName("REG"));

		String OGG = "OGG";
		String REG = "REG";
		String SPS_RISTO = "SPS.RISTO";
		String SPS = "SPS";
		String SPS_CIBO = "SPS.CIBO";
		String VAR_EXTRA = "VAR.EXTRA";
		String TAX = "TAX";
		String MTR = "MTR";
		String PROF = "PROF";
		String COM_PRIXTEL = "COM.PRIXTEL";
		String BAN_BPN = "BAN.BPN";
		String BAN_BNP = "BAN.BNP";
		String CAS_AFF = "CAS.AFF";

		sectors = new ArrayList<>();

		sectors.add(REG);
		sectors.add(OGG);
		sectors.add(VAR_EXTRA);
		sectors.add(SPS_CIBO);
		sectors.add(SPS_RISTO);
		sectors.add(SPS);
		sectors.add(TAX);
		sectors.add(MTR);
		sectors.add(PROF);
		sectors.add(COM_PRIXTEL);
		sectors.add(BAN_BPN);
		sectors.add(BAN_BNP);
		sectors.add(CAS_AFF);

		int id = 0;

		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"VIRT CPTE A CPTE EMIS SUR LE PEL30004020370007624908154 - OTHER DATA - [CATEGORIE : Non défini]")
				.setSectorName(MTR));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("COMMISSIONS COTISATION ESPRIT LIBRE - OTHER DATA - [CATEGORIE : Opérations bancaires]")
				.setSectorName(BAN_BNP));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("PRLV SEPA PRIXTEL ECH/040118 ID EMETTEUR/FR48PXL48 - OTHER DATA - [CATEGORIE : Non défini]")
				.setSectorName(COM_PRIXTEL));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"FACTURE CARTE DU 040118 DADARO SRL CARTE 4974XXXXXXXX8 - OTHER DATA - [CATEGORIE : Alimentation]")
				.setSectorName(VAR_EXTRA));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("FACTURE CARTE DU 070118 GIUSEPPE LA BUF CARTE 4974XXXX - OTHER DATA - [CATEGORIE : Sorties]")
				.setSectorName(SPS_RISTO));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"FACTURE CARTE DU 040118 PROGRESS CARTE 4974XXXXXXXX806 - OTHER DATA - [CATEGORIE : Consommation]")
				.setSectorName(OGG));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"FACTURE CARTE DU 070118 373985PX RELAY NICE CARTE 4974 - OTHER DATA - [CATEGORIE : Consommation]")
				.setSectorName(OGG));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle(
						"FACTURE CARTE DU 070118 ACA BUS NICE CEDEX 3 CARTE 497 - OTHER DATA - [CATEGORIE : Transport]")
				.setSectorName(VAR_EXTRA));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"VIREMENT SEPA EMIS /MOTIF LOYER JANVIER 2018, APPART 3P, PA - OTHER DATA - [CATEGORIE : Non défini]")
				.setSectorName(CAS_AFF));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle(
						"FACTURE CARTE DU 080118 SNCF JUAN LES PINS CARTE 4974X - OTHER DATA - [CATEGORIE : Transport]")
				.setSectorName(VAR_EXTRA));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"imposta di bollo cc e lr - da 01/10/2017 a 31/12/2017 - OTHER DATA - [DATA VALUTA : 09/01/2018], [DIVISA : EUR], [CANALE : ]")
				.setSectorName(BAN_BPN));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("FACTURE CARTE DU 100118 SODEXO FR602609 VILLENEUVE LO - OTHER DATA - [CATEGORIE : Sorties]")
				.setSectorName(SPS_CIBO));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"VIR SEPA RECU /DE SNCF MOBILITES VOYAGES RMBT /MOTIF S - OTHER DATA - [CATEGORIE : Non défini]")
				.setSectorName(VAR_EXTRA));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"FACTURE CARTE DU 130118 CROCS ANTIBES ANTIBES CARTE 49 - OTHER DATA - [CATEGORIE : Habillement]")
				.setSectorName(OGG));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("PRLV SEPA D.G.F.I.P. IMPOT 06101 ECH/150118 ID EME - OTHER DATA - [CATEGORIE : Impôts]")
				.setSectorName(TAX));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle(
						"VIR SEPA RECU /DE ALTRAN TECHNOLOGIES /MOTIF VIRT NDF - OTHER DATA - [CATEGORIE : Non défini]")
				.setSectorName(PROF));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("FACTURE CARTE DU 200118 QPF PECHEURS BS ANTIB2451773/ - OTHER DATA - [CATEGORIE : Voiture]")
				.setSectorName(VAR_EXTRA));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"FACTURE CARTE DU 200118 VANESSA TISSUS ANTIBES CARTE 4 - OTHER DATA - [CATEGORIE : Habillement]")
				.setSectorName(OGG));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"FACTURE CARTE DU 200118 ANTIDIS MONDEVILLE CARTE 4974X - OTHER DATA - [CATEGORIE : Alimentation]")
				.setSectorName(VAR_EXTRA));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"FACTURE CARTE DU 270118 CARREFOUR TPE CARTE 4974XXXXXX - OTHER DATA - [CATEGORIE : Alimentation]")
				.setSectorName(SPS));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"FACTURE CARTE DU 270118 CARREFOUR TPE CARTE 4974XXXXXX - OTHER DATA - [CATEGORIE : Alimentation]")
				.setSectorName(SPS));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("FACTURE CARTE DU 270118 CARREFOUR DAC CARTE 4974XXXXXX - OTHER DATA - [CATEGORIE : Voiture]")
				.setSectorName(SPS));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"FACTURE CARTE DU 270118 A.AFFLELOU ANTIBES CARTE 4974X - OTHER DATA - [CATEGORIE : Consommation]")
				.setSectorName(VAR_EXTRA));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("FACTURE CARTE DU 260118 SODEXO FR602609 VILLENEUVE LO - OTHER DATA - [CATEGORIE : Sorties]")
				.setSectorName(SPS_CIBO));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle(
						"FACTURE CARTE DU 290118 SNCF JUAN LES PINS CARTE 4974X - OTHER DATA - [CATEGORIE : Transport]")
				.setSectorName(VAR_EXTRA));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"VIRT CPTE A CPTE EMIS SUR LE PEL30004020370007624908154 - OTHER DATA - [CATEGORIE : Non défini]")
				.setSectorName(MTR));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"VIR SEPA RECU /DE ALTRAN TECHNOLOGIES /MOTIF SALAIRE D - OTHER DATA - [CATEGORIE : Non défini]")
				.setSectorName(PROF));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"VIREMENT SEPA EMIS /MOTIF LOYER FEVRIER 2018, APPART 3P, PA - OTHER DATA - [CATEGORIE : Non défini]")
				.setSectorName(CAS_AFF));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("COMMISSIONS COTISATION ESPRIT LIBRE - OTHER DATA - [CATEGORIE : Opérations bancaires]")
				.setSectorName(BAN_BNP));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("PRLV SEPA PRIXTEL ECH/060218 ID EMETTEUR/FR48PXL48 - OTHER DATA - [CATEGORIE : Non défini")
				.setSectorName(COM_PRIXTEL));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"FACTURE CARTE DU 100218 CARREFOUR TPE CARTE 4974XXXXXX - OTHER DATA - [CATEGORIE : Alimentation]")
				.setSectorName(SPS));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("PRLV SEPA D.G.F.I.P. IMPOT 06101 ECH/150218 ID EME - OTHER DATA - [CATEGORIE : Impôts]")
				.setSectorName(TAX));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle(
						"VIR SEPA RECU /DE ALTRAN TECHNOLOGIES /MOTIF VIRT NDF - OTHER DATA - [CATEGORIE : Non défini]")
				.setSectorName(PROF));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"FACTURE CARTE DU 180218 QYZ GRASSE CARTE 4974XXXXXXXX8 - OTHER DATA - [CATEGORIE : Alimentation]")
				.setSectorName(SPS_CIBO));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("FACTURE CARTE DU 180218 ESCOT ANTIBES O MANDELIEU LA C - OTHER DATA - [CATEGORIE : Voiture]")
				.setSectorName(VAR_EXTRA));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"FACTURE CARTE DU 180218 DUMANOIS PRIMEU VALBONNE CARTE - OTHER DATA - [CATEGORIE : Alimentation]")
				.setSectorName(SPS_CIBO));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"FACTURE CARTE DU 240218 CARREFOUR TPE CARTE 4974XXXXXX - OTHER DATA - [CATEGORIE : Alimentation]")
				.setSectorName(SPS));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"VIR SEPA RECU /DE ALTRAN TECHNOLOGIES /MOTIF SALAIRE D - OTHER DATA - [CATEGORIE : Non défini]")
				.setSectorName(PROF));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"VIRT CPTE A CPTE EMIS SUR LE PEL30004020370007624908154 - OTHER DATA - [CATEGORIE : Non défini]")
				.setSectorName(MTR));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"VIREMENT SEPA EMIS /MOTIF LOYER MARS 2018, APPART 3P, PARC - OTHER DATA - [CATEGORIE : Non défini]")
				.setSectorName(CAS_AFF));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("FACTURE CARTE DU 280218 ESCOT MANDELIEU LA CARTE 4974X - OTHER DATA - [CATEGORIE : Voiture]")
				.setSectorName(VAR_EXTRA));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("COMMISSIONS COTISATION ESPRIT LIBRE - OTHER DATA - [CATEGORIE : Opérations bancaires]")
				.setSectorName(BAN_BNP));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("FACTURE CARTE DU 040318 SEMIACS NICE CARTE 4974XXXXXXX - OTHER DATA - [CATEGORIE : Voiture]")
				.setSectorName(VAR_EXTRA));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"FACTURE CARTE DU 030318 CARREFOUR TPE CARTE 4974XXXXXX - OTHER DATA - [CATEGORIE : Alimentation]")
				.setSectorName(SPS));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle(
						"FACTURE CARTE DU 020318 SNCF JUAN LES PINS CARTE 4974X - OTHER DATA - [CATEGORIE : Transport]")
				.setSectorName(VAR_EXTRA));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("FACTURE CARTE DU 040318 ESCOT MANDELIEU LA CARTE 4974X - OTHER DATA - [CATEGORIE : Voiture]")
				.setSectorName(VAR_EXTRA));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("FACTURE CARTE DU 020318 SODEXO FR602609 VILLENEUVE LO - OTHER DATA - [CATEGORIE : Sorties]")
				.setSectorName(SPS_CIBO));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"FACTURE CARTE DU 030318 JEAN LUC PELE / ANTIBES CARTE - OTHER DATA - [CATEGORIE : Alimentation]")
				.setSectorName(SPS_CIBO));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("FACTURE CARTE DU 040318 FOURNIL GRENADI ANTIBES CARTE - OTHER DATA - [CATEGORIE : Sorties]")
				.setSectorName(SPS_CIBO));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("PRLV SEPA PRIXTEL ECH/050318 ID EMETTEUR/FR48PXL48 - OTHER DATA - [CATEGORIE : Non défini]")
				.setSectorName(COM_PRIXTEL));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle(
						"FACTURE CARTE DU 100318 MDL ANTIBES ANTIBES CARTE 4974 - OTHER DATA - [CATEGORIE : Logement]")
				.setSectorName(VAR_EXTRA));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle(
						"FACTURE CARTE DU 110318 ACA CAISSES AUT NICE CEDEX 3 C - OTHER DATA - [CATEGORIE : Vacances]")
				.setSectorName(VAR_EXTRA));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("FACTURE CARTE DU 110318 ESCOT MANDELIEU LA CARTE 4974X - OTHER DATA - [CATEGORIE : Voiture]")
				.setSectorName(VAR_EXTRA));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"FACTURE CARTE DU 100318 CARREFOUR TPE CARTE 4974XXXXXX - OTHER DATA - [CATEGORIE : Alimentation]")
				.setSectorName(SPS));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++)).setTitle(
				"FACTURE CARTE DU 100318 CARREFOUR TPE CARTE 4974XXXXXX - OTHER DATA - [CATEGORIE : Alimentation]")
				.setSectorName(SPS));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("FACTURE CARTE DU 110318 CARREFOUR DAC CARTE 4974XXXXXX - OTHER DATA - [CATEGORIE : Voiture]")
				.setSectorName(SPS));
		transactions.add((DetailedTransaction) new DetailedTransaction().setId(String.valueOf(id++))
				.setTitle("PRLV SEPA D.G.F.I.P. IMPOT 06101 ECH/150318 ID EME - OTHER DATA - [CATEGORIE : Impôts]")
				.setSectorName(TAX));

		bta.refreshTokensMap(transactions, sectors);
	}

	@Test
	public void suggestREGOk() {
		List<String> sectors = bta.suggestSectorsForTransaction(new DetailedTransaction().setId("1")
				.setTitle("pagamento internet - carta*5842-12:21-mgp*leetchi.com0355938 paris fra")
				.setSectorName("REG"), 3, SimilarityRelevance.HIGH);
		sectors.stream().forEach(s -> System.out.println("[" + s + "]"));
		assertEquals("REG", sectors.get(0));
	}

	@Test
	public void suggestSPSOk() {
		List<String> sectors = bta.suggestSectorsForTransaction(new DetailedTransaction().setId("1")
				.setTitle("PAIEMENT CB CARREFOUR (FRANCE) DU 25/10").setSectorName("REG"), 3, SimilarityRelevance.HIGH);
		sectors.stream().forEach(s -> System.out.println("[" + s + "]"));
		assertEquals("SPS", sectors.get(0));
	}

	@Test
	public void suggestSPSCIBOOk() {
		List<String> sectors = bta.suggestSectorsForTransaction(new DetailedTransaction().setId("1")
				.setTitle("FACTURE CARTE DU 260118 SODEXO FR602609 VILLENEUVE LO - OTHER DATA - [CATEGORIE : Sorties]")
				.setSectorName("REG"), 3, SimilarityRelevance.HIGH);
		sectors.stream().forEach(s -> System.out.println("[" + s + "]"));
		assertEquals("SPS.CIBO", sectors.get(0));
	}

}
