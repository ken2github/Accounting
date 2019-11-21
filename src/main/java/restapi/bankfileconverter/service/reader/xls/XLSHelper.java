package restapi.bankfileconverter.service.reader.xls;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class XLSHelper {

	public static InputStream testLocal_BNP_file_asByteArray() {
		return getInputSteam_from_localFile("C:\\Users\\primo\\Downloads\\export_01_03_2019_21_10_56.xls");
	}

	public static InputStream testLocal_LA_file_asByteArray() {
		return getInputSteam_from_localFile("C:\\Users\\primo\\Downloads\\la.2018.07.08.09.balance.22896.01.xls");
	}

	public static InputStream testLocal_LDD_file_asByteArray() {
		return getInputSteam_from_localFile("C:\\Users\\primo\\Downloads\\ldd.2018.07.08.09.balance.12067.81.xls");
	}

	public static InputStream testLocal_PEL_file_asByteArray() {
		return getInputSteam_from_localFile("C:\\Users\\primo\\Downloads\\pel.2018.07.08.09.balance.61810.75.xls");
	}

	public static InputStream testLocal_INTERNAL_file_asByteArray() {
		return getInputSteam_from_localFile("C:\\Users\\primo\\Downloads\\internal_1_0.xls");
	}

	public static InputStream testLocal_EDENRED_file_asByteArray() {
		return getInputSteam_from_localFile("C:\\Users\\primo\\Downloads\\edenred_1_0.xls");
	}

	public static InputStream testLocal_BPN_file_asByteArray() {
		return getInputSteam_from_localFile("C:\\Users\\primo\\Downloads\\movimentiConto2.xls");
	}

	public static InputStream testLocal_YOUCARD_file_asByteArray() {
		return getInputSteam_from_localFile("C:\\Users\\primo\\Downloads\\movimenti.xls");
	}

	public static InputStream testLocal_SPESE_file_asByteArray() {
		return getInputSteam_from_localFile("C:\\Users\\primo\\Downloads\\spese2019.xls");
	}

	public static InputStream getInputSteam_from_localFile(String filePath) {
		// read bytes from local file
		Path path = Paths.get(filePath);
		try {
			byte[] bArray = Files.readAllBytes(path);
			return new ByteArrayInputStream(bArray);
		} catch (IOException e) {
			throw new RuntimeException("NOT_IMPLEMENTED: " + e.getMessage());
		}
	}

	public static void exploreTestContent(InputStream is) {
		try {
			// take input from such array of bytes
			POIFSFileSystem fs = new POIFSFileSystem(is);
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);
			HSSFRow row;
			HSSFCell cell;

			int rows; // No of rows
			rows = sheet.getPhysicalNumberOfRows();

			int cols = 0; // No of columns
			int tmp = 0;

			// This trick ensures that we get the data properly even if it doesn't start
			// from first few rows
			for (int i = 0; i < 10 || i <= rows; i++) {
				row = sheet.getRow(i);
				if (row != null) {
					tmp = sheet.getRow(i).getPhysicalNumberOfCells();
					if (tmp > cols)
						cols = tmp;
				}
			}

			for (int r = 0; r <= rows; r++) {
				row = sheet.getRow(r);
				if (row != null) {
					for (int c = 0; c < cols; c++) {
						cell = row.getCell((short) c);
						if (cell != null) {
							switch (cell.getCellType()) {
							case HSSFCell.CELL_TYPE_STRING:
								System.out.println("[" + r + "," + c + "]=[STR]=[" + cell.getStringCellValue() + "]");
								break;
							case HSSFCell.CELL_TYPE_BLANK:
								System.out.println("[" + r + "," + c + "]=[BLA]=[" + "EMPTY CELL" + "]");
								break;
							case HSSFCell.CELL_TYPE_BOOLEAN:
								System.out.println("[" + r + "," + c + "]=[BOO]=[" + cell.getBooleanCellValue() + "]");
								break;
							case HSSFCell.CELL_TYPE_ERROR:
								System.out.println("[" + r + "," + c + "]=[ERR]=[" + cell.getErrorCellValue() + "]");
								break;
							case HSSFCell.CELL_TYPE_FORMULA:
								System.out.println("[" + r + "," + c + "]=[FOR]=[" + cell.getCellFormula() + "]");
								break;
							case HSSFCell.CELL_TYPE_NUMERIC:
								System.out.println("[" + r + "," + c + "]=[NUM]=[" + cell.getNumericCellValue() + "]");
								break;
							}
						}
					}
				}
			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
	}

}
