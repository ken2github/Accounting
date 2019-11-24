package controllers;

import static deprecated.store.io.Format.RAW_SEPARATOR_REGEXP;
import static restapi.bankfileconverter.api.Count.ASSMUL;
import static restapi.bankfileconverter.api.Count.BNP;
import static restapi.bankfileconverter.api.Count.BPN;
import static restapi.bankfileconverter.api.Count.EDENRED;
import static restapi.bankfileconverter.api.Count.FRANCA;
import static restapi.bankfileconverter.api.Count.HOLD;
import static restapi.bankfileconverter.api.Count.LA;
import static restapi.bankfileconverter.api.Count.LDD;
import static restapi.bankfileconverter.api.Count.MONEY;
import static restapi.bankfileconverter.api.Count.PEL;
import static restapi.bankfileconverter.api.Count.TKTRESTO;
import static restapi.bankfileconverter.api.Count.YOUCARD;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import controllers.exceptionhandler.ApiError;
import controllers.exceptionhandler.ApiError.ApiErrorCode;
import controllers.exceptionhandler.ApiException;
import dao.DAOException;
import dao.FileReader;
import dao.ReadOnlyTransactionList;
import model2.Count;
import model2.DetailedCount;
import model2.DetailedSector;
import model2.DetailedTransaction;
import model2.Sector;
import model2.Transaction;

@RestController
@RequestMapping("/db")
public class InitDBAPIController {

	Logger logger = LoggerFactory.getLogger(InitDBAPIController.class);

	@Autowired
	private AccountedYearsAPIController accountedyearsAPIC;

	@Autowired
	private SectorsAPIController sectorsAPIC;

	@Autowired
	private CountsAPIController countsAPIC;

	@Autowired
	private TransactionsAPIController transactionsAPIC;

	@Autowired
	private StagedTransactionsAPIController stagedTransactionsAPIC;

	@Autowired
	private FileReader fileReader;

	private static List<Integer> YEARS = Arrays.asList(2017, 2018, 2019);
	private static List<Integer> MONTHS = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);

	private static Date parseDate(String date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
			throw new ApiException(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ApiErrorCode.INTERNAL_ERROR,
					String.format("Error in parsing date '%d' with format \"yyyy-MM-dd\"", date), e.getMessage()));
		}

	}

	@PostMapping("/uploadFile")
	public boolean uploadFile(@RequestParam("file") MultipartFile file) {
		// Normalize file name
		String fileName = file.getOriginalFilename();
		System.out.println(fileName);

		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new ApiException(
						new ApiError().setErrorCode(ApiErrorCode.INVALID_INPUT).setHttpStatus(HttpStatus.BAD_REQUEST)
								.setExternalMessage("Filename 'fileName' contains invalid path sequence."));
			}

			// Parse filename to get count, year, months
			String[] items = fileName.split(RAW_SEPARATOR_REGEXP);
			String count = items[0];
			int year = Integer.parseInt(items[1]);
			int months = items.length - 6; // with balance
			List<Date> nominalMonths = new ArrayList<>();
			for (int i = 0; i < months; i++) {
				nominalMonths.add(parseDate(String.format("%s-%s-01", year, items[2 + i])));
			}

			ReadOnlyTransactionList transactionList = fileReader.getReadOnlyTransactionList(file.getInputStream(),
					count, nominalMonths, fileName);
			for (Transaction transaction : transactionList.getTransactions()) {
				transactionsAPIC.createTransaction(transaction);
			}
		} catch (DAOException ex) {
			throw new ApiException(ApiError.parseFromDBError(ex.getDBError()));
			// "Could not store file " + fileName + ". Please try again!", ex);
		} catch (IOException e) {
			String message = String.format("Input file '%s' cannot be opened.", fileName);
			throw new ApiException(new ApiError(HttpStatus.BAD_REQUEST, ApiErrorCode.INVALID_INPUT, message, message));
		}

		return true;
	}

	@RequestMapping(method = RequestMethod.POST)
	public void initDB() {

		try {
			// Create YEARS
			for (Integer year : YEARS) {
				accountedyearsAPIC.createAccountedYear(year);
			}

			// Create SECTORS
			Map<String, List<String>> sectors = new HashMap<String, List<String>>();

			sectors.put("SPS-Spesa", Arrays.asList("CIBO-cibo", "RISTO-risto", "BAGNO-bagno", "CASA-casa"));

			sectors.put("CAS-Casa",
					Arrays.asList("AFFITTO-affitto", "ENERGIA-energia", "ACQUA-acqua", "PULIZIE-pulizie", "TASSE-tasse", // ?????
							"MOBILI-mobili", "COM-com"));

			sectors.put("TAS-Tasse", Arrays.asList("FISCO-fisco"));

			sectors.put("VAR-Varie", Arrays.asList("SPORT-sport", "VIAGGI-viaggi", "EVENTI-eventi",
					"BABYGUARD-babyguard", "BABYACT-babyact"));

			sectors.put("CUR-Cure", Arrays.asList("SALUTE-salute", "ESTETICA-estetica"));

			sectors.put("OGG-Oggetti", Arrays.asList("LIBRI-libri", "ELETTRO-elettro", "VESTITI-vestiti"));

			sectors.put("CAR-Macchina",
					Arrays.asList("MANUT-manut", "ASSIC-assic", "BENZINA-benzina", "BOLLO-bollo", "CASELLI-caselli"));

			sectors.put("COM-Comunicazioni", Arrays.asList("PRIXTEL-PRIXTEL", "WIND-WIND"));

			sectors.put("REG-Regali", Arrays.asList("FAMIGLIA-famiglia", "AFFITTO-affitto babbo", "AMICI-amici"));

			sectors.put("BAN-Banca", Arrays.asList("PARIS-paris", "NOVARA-novara"));

			sectors.put("PROF-Professione", Arrays.asList("MISSIONE-missione", "FORMAZ-formaz"));

			sectors.put("MTR-Money transfer", Arrays.asList());

			sectors.keySet().stream().forEach(superSector -> {
				String[] superSectorItems = superSector.split("-");
				String superSectorName = superSectorItems[0];
				String superSectorDescription = superSectorItems[1];

				// for each superSector create two sectors: Generic and Extra
				System.out.println(
						String.format("SupSector=[%s],SupSectorDesc=[%s]", superSectorName, superSectorDescription));

				createSector(superSectorName, superSectorName, superSectorDescription + " generica");
				createSector(superSectorName + "." + "EXTRA", superSectorName, superSectorDescription + " extra");

				sectors.get(superSector).stream().forEach(sector -> {
					String[] sectorItems = sector.split("-");
					String sectorName = superSectorName + "." + sectorItems[0];
					String sectorDescription = superSectorName + " " + sectorItems[1];

					System.out.println(String.format("Sector=[%s],SupSector=[%s],SectorDesc=[%s]", sectorName,
							superSectorName, sectorDescription));

					createSector(sectorName, superSectorName, sectorDescription);
				});
			});

			// DetailedSector sps_sector = createSector("SPS", "SPS", "Spesa generica");
			// DetailedSector sps_cibo_sector = createSector("SPS.CIBO", "SPS", "Spesa
			// cibo");

			// Create COUNTS
			DetailedCount bnp_count = createCount(BNP.name(), "Banque National de Paris");
			DetailedCount ldd_count = createCount(LDD.name(), "Livret Developpement Durable");
			DetailedCount la_count = createCount(LA.name(), "Livret A");
			DetailedCount pel_count = createCount(PEL.name(), "Plan Epargnement Logement");
			DetailedCount assmul_count = createCount(ASSMUL.name(), "Assurance et Multiplacement");
			DetailedCount bpn_count = createCount(BPN.name(), "Banca Popolare di Novara");
			DetailedCount youcard_count = createCount(YOUCARD.name(), "Carta di debito Youcard");
			DetailedCount edenred_count = createCount(EDENRED.name(), "Conto Edenred");
			DetailedCount tktresto_count = createCount(TKTRESTO.name(), "Ticket resto cartacei");
			DetailedCount money_count = createCount(MONEY.name(), "Soldi in moneta liquida");
			DetailedCount hold_count = createCount(HOLD.name(), "Soldi bloccati da qualche parte");
			DetailedCount franca_count = createCount(FRANCA.name(), "Banco Franca");

			// Create TRANSACTIONS to have balance for all counts since 31/12/2017
			initCountBalanceWithTransaction(bnp_count, new BigDecimal("31159.18"));
			initCountBalanceWithTransaction(ldd_count, new BigDecimal("12067.81"));
			initCountBalanceWithTransaction(la_count, new BigDecimal("22896.01"));
			initCountBalanceWithTransaction(pel_count, new BigDecimal("61315.75"));
			initCountBalanceWithTransaction(assmul_count, new BigDecimal("1032.72"));
			initCountBalanceWithTransaction(bpn_count, new BigDecimal("11956.02"));
			initCountBalanceWithTransaction(youcard_count, new BigDecimal("3076.52"));
			initCountBalanceWithTransaction(edenred_count, new BigDecimal("1511.05"));
			initCountBalanceWithTransaction(tktresto_count, new BigDecimal("486.00"));
			initCountBalanceWithTransaction(money_count, new BigDecimal("6872.58"));
			initCountBalanceWithTransaction(hold_count, new BigDecimal("4.75"));
			initCountBalanceWithTransaction(franca_count, new BigDecimal("5787.33"));

			// 35.996,54

			// createTransaction("2019-01-01", -100L, moneta_count.getName(), "Cibo mio",
			// sps_cibo_sector.getName(),
			// false);
			// createTransaction("2019-01-01", -100L, bnp_count.getName(), "Prestito a F",
			// mtr_sector.getName(), true);
			// createTransaction("2019-01-01", 200L, bnp_count.getName(), "Prestito da F",
			// mtr_sector.getName(), true);

			// createTransaction("2019-01-01", 100L, bnp_count.getName(), "Titolo",
			// sps_sector.getName(), true);
			// createTransaction("2019-01-01", 100L, bnp_count.getName(), "Titolo",
			// sps_sector.getName(), true);
			// createTransaction("2019-01-01", 100L, bnp_count.getName(), "Titolo",
			// sps_sector.getName(), true);
			// createTransaction("2019-01-01", 100L, bnp_count.getName(), "Titolo",
			// sps_sector.getName(), true);
			// createTransaction("2019-01-01", 100L, bnp_count.getName(), "Titolo",
			// sps_sector.getName(), true);

		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
	}

	private void initCountBalanceWithTransaction(DetailedCount count, BigDecimal initialBalance) {
		transactionsAPIC.createTransaction(new Transaction().setDate(parseDate("2017-12-31")).setAmount(initialBalance)
				.setCountName(count.getName()).setTitle("Apertura conto").setSectorName("VAR.EXTRA")
				.setIsCommon(false));
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public void resetDB() {
		try {
			// Reset STAGED TRANSACTIONS
			List<DetailedTransaction> stagedTransactions = stagedTransactionsAPIC.findAll();
			for (DetailedTransaction transaction : stagedTransactions) {
				stagedTransactionsAPIC.deleteTransaction(transaction.getId());
			}

			// Reset TRANSACTIONS
			List<DetailedTransaction> transactions;
			for (Integer year : YEARS) {
				for (Integer month : MONTHS) {
					transactions = transactionsAPIC.findByYearMonth(year, month);
					for (DetailedTransaction transaction : transactions) {
						transactionsAPIC.deleteTransaction(transaction.getId());
					}
				}
			}

			// Reset COUNTS
			List<DetailedCount> counts = countsAPIC.findAll();
			for (DetailedCount count : counts) {
				countsAPIC.deleteByName(count.getName());
			}

			// Reset SECTORS
			List<DetailedSector> sectors = sectorsAPIC.findAll();
			for (DetailedSector sector : sectors) {
				if (!sector.getName().equals(sector.getFatherName()))
					sectorsAPIC.deleteByName(sector.getName());
			}
			sectors = sectorsAPIC.findAll();
			for (DetailedSector sector : sectors) {
				sectorsAPIC.deleteByName(sector.getName());
			}

			// Reset YEARS
			List<Integer> accountedYears = accountedyearsAPIC.findAll();
			for (Integer year : accountedYears) {
				accountedyearsAPIC.deleteAccountedYear(year);
			}
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
	}

	private DetailedSector createSector(String name, String fatherName, String description) {
		return sectorsAPIC.createSector(new Sector().setActivationDate(parseDate("2017-01-01"))
				.setDeactivationDate(null).setName(name).setFatherName(fatherName).setDescription(description));
	}

	private DetailedCount createCount(String name, String description) {
		return countsAPIC.createCount(new Count().setOpenDate(parseDate("2017-01-01")).setCloseDate(null).setName(name)
				.setDescription(description));
	}

	// private DetailedTransaction createTransaction(String date, BigDecimal amount,
	// String countName, String title,
	// String sectorName, boolean isCommon) {
	// return transactionsAPIC.createTransaction(new
	// Transaction().setDate(parseDate(date)).setAmount(amount)
	// .setCountName(countName).setTitle(title).setSectorName(sectorName).setIsCommon(isCommon));
	// }

}
