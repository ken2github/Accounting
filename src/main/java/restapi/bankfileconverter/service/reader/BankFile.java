package restapi.bankfileconverter.service.reader;

import java.io.InputStream;

import restapi.bankfileconverter.api.Count;
import restapi.bankfileconverter.service.reader.xls.BNP_1_0_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.BPN_1_0_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.Edenred_1_0_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.Internal_1_0_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.Spese_1_0_XLS_Reader;
import restapi.bankfileconverter.service.reader.xls.YOUCARD_1_0_XLS_Reader;

public class BankFile {

	public static BankFileReader getReader(BankFileFormat fileFormat, InputStream is) {
		switch (fileFormat) {
		case BNP_1_0:
			return new BNP_1_0_XLS_Reader(is);
		case BPN_1_0:
			return new BPN_1_0_XLS_Reader(is);
		case YOUCARD_1_0:
			return new YOUCARD_1_0_XLS_Reader(is);
		case EDENRED_1_0:
			return new Edenred_1_0_XLS_Reader(is);
		case INTERNAL_1_0:
			return new Internal_1_0_XLS_Reader(is);
		case SPESE_1_0:
			return new Spese_1_0_XLS_Reader(is);
		default:
		}
		throw new RuntimeException("NOT_IMPLEMENTED");
	}

	public static BankFileFormat getFileFormat(Count count) {
		switch (count) {
		case BNP:
		case LDD:
		case LA:
		case PEL:
			return BankFileFormat.BNP_1_0;
		case BPN:
			return BankFileFormat.BPN_1_0;
		case YOUCARD:
			return BankFileFormat.YOUCARD_1_0;
		case EDENRED:
			return BankFileFormat.EDENRED_1_0;
		case TKTRESTO:
		case ASSMUL:
		case HOLD:
		case FRANCA:
			return BankFileFormat.INTERNAL_1_0;
		case MONEY:
			return BankFileFormat.SPESE_1_0;
		default:
			throw new RuntimeException("NOT_IMPLEMENTED");
		}
	}

}
