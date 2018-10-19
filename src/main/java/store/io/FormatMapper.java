package store.io;

import store.io.readers.Parametric_FormatReader;

public class FormatMapper {

	public static RecordMapper BNP_FORMAT = new RecordMapper(0, 3, 2, 4, 5, "dd-MM-yyyy", 6, 3); 
	public static RecordMapper BPN_FORMAT = new RecordMapper(0, 2, 4, 5, 6, "dd/MM/yyyy", 7, 1); 
	
	//0  1         2                                                                   3                     4        5   6
	// ,05/10/18,"23h55 - RESTAU ACCHIARD NICE,FRA Dépassement du plafond quotidien ",transaction refusée ,"€ 17,00",jhg, jhg
	
	
	// 05/10/18,"12h49 - SODEXO FR600881 MOUGINS,FRA  MOUGINS ",transaction en cours de traitement ,"-8,20",sps.cibo,y
	public static RecordMapper EDENRED_FORMAT = new RecordMapper(0, 3, 1, 4, 5, "dd/MM/yy", 6, 0); 
	public static RecordMapper CUSTOM_FORMAT= new RecordMapper(0, 1, 2, 3, 4, "dd/MM/yyyy", 5, 1); 
	
	public static FormatReader getFormatReader(String count) throws UnmappedFormatReaderException {
		RecordMapper recordMapper=null;
		
		String lc_count = count.toLowerCase();
		
		switch (lc_count) {
		case "bnp":	recordMapper=BNP_FORMAT;
			break;		
		case "ldd":	recordMapper=BNP_FORMAT;	
			break;
		case "pel":	recordMapper=BNP_FORMAT;		
			break;
		case "la":	recordMapper=BNP_FORMAT;	
			break;
		case "ede":	recordMapper=EDENRED_FORMAT;
			break;
		case "tik":	recordMapper=CUSTOM_FORMAT;		
			break;
		case "franca":	recordMapper=CUSTOM_FORMAT;		
			break;
		case "mon":	recordMapper=CUSTOM_FORMAT;		
			break;
		case "bpn":	recordMapper=BPN_FORMAT;		
			break;
		case "you":	recordMapper=BPN_FORMAT;		
			break;
		default:
			throw new UnmappedFormatReaderException();
		}
		
		return new Parametric_FormatReader(recordMapper);
	}
	
}
