package deprecated.reporting;

import java.io.File;
import java.util.List;

import deprecated.model.books.YearBook;

public interface Reportable {

	enum Color{
		GREEN,RED,BLUE;
	}
	
	void createReport(YearBook yb,File outputDirectory) throws ReportException;
	
	String createTableReport(List<List<String>> rows, String caption, Color captionColor) throws ReportException;
	
}
