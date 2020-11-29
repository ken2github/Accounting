package restapi.bankfileconverter.service.reader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import restapi.bankfileconverter.api.Count;
import restapi.bankfileconverter.service.reader.csv.Edenred_2_0_CSV_Reader;
import restapi.bankfileconverter.service.reader.csv.Edenred_3_0_CSV_Reader;
import restapi.bankfileconverter.service.reader.xls.BNP_1_0_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.BPN_1_0_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.BPN_1_1_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.Edenred_1_0_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.Internal_1_0_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.Internal_2_0_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.Spese_1_0_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.YOUCARD_1_0_XLS_Reader;

public class BankFile {

	public static BankFileReader getReader(BankFileFormat fileFormat, InputStream is) {
		switch (fileFormat) {
		case BNP_1_0:
			return new BNP_1_0_XLS_Reader(is);
		case BPN_1_0:
			return new BPN_1_0_XLS_Reader(is);
		case BPN_1_1:
			return new BPN_1_1_XLS_Reader(is);
		case YOUCARD_1_0:
			return new YOUCARD_1_0_XLS_Reader(is);
		case EDENRED_1_0:
			return new Edenred_1_0_XLS_Reader(is);
		case EDENRED_2_0:
			return new Edenred_2_0_CSV_Reader((Reader) new InputStreamReader(is));
		case EDENRED_3_0:
			return new Edenred_3_0_CSV_Reader((Reader) new InputStreamReader(is));
		case INTERNAL_1_0:
			return new Internal_1_0_XLS_Reader(is);
		case INTERNAL_2_0:
			return new Internal_2_0_XLS_Reader(is);
		case SPESE_1_0:
			return new Spese_1_0_XLS_Reader(is);
		default:
		}
		throw new RuntimeException("NOT_IMPLEMENTED");
	}

	public static List<BankFileFormat> getFileFormat(Count count) {
		switch (count) {
		case BNP:
		case LDD:
		case LA:
		case PEL:
			return Arrays.asList(BankFileFormat.BNP_1_0);
		case BPN:
			return Arrays.asList(BankFileFormat.BPN_1_0, BankFileFormat.BPN_1_1);
		case YOUCARD:
			return Arrays.asList(BankFileFormat.YOUCARD_1_0);
		case EDENRED:
			return Arrays.asList(BankFileFormat.EDENRED_1_0, BankFileFormat.EDENRED_2_0, BankFileFormat.EDENRED_3_0);
		case TKTRESTO:
		case ASSMUL:
		case HOLD:
			return Arrays.asList(BankFileFormat.INTERNAL_1_0);
		case FRANCA:
			return Arrays.asList(BankFileFormat.INTERNAL_2_0);
		case MONEY:
			return Arrays.asList(BankFileFormat.SPESE_1_0);
		default:
			throw new RuntimeException("NOT_IMPLEMENTED");
		}
	}

}
