package deprecated.model.books;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Vector;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MasterBook {

	@Value("${com.verdino.balancing.model.db.type}")
	private String typeOfsource;
	
	@Value("${com.verdino.balancing.model.db.source}")
	private String source;
	
	private Vector<YearBook> yearBooks = new Vector<>();

	public MasterBook() {
		System.out.println("Construction done");
	}
	
	public MasterBook(File dbdirectory) throws IOException, ParseException, NotAlignedDateInFileException, Exception {
		initFromFileSystem(dbdirectory);
	}
	
	@PostConstruct
	public void init() throws Exception {
		if(typeOfsource.equals("filesystem.local")) {
			initFromFileSystem(new File(this.source));
			System.out.println("Init done");
		}else {
			throw new Exception("Type of Source and Source not recognized");
		}
	}
	
	private void initFromFileSystem(File dbdirectory) throws IOException, ParseException, NotAlignedDateInFileException, Exception {
		if(!dbdirectory.isDirectory()) {
			throw new Exception("The provided filename ["+dbdirectory.getAbsolutePath()+"] is not a directory");
		}
		
		// read all data
		File[] files = dbdirectory.listFiles(new FileFilter(){				
			@Override
			public boolean accept(File pathname) {
				return (pathname.isDirectory() && pathname.getName().matches("^[\\d]*$"));
			}			
		});
		// fill in count transactions 
		for(File s: files) {
			this.yearBooks.addElement(new YearBook(s));

		}
	}

	public Vector<YearBook> getYearBooks() {
		return yearBooks;
	}

	public String getTypeOfsource() {
		return typeOfsource;
	}

	public void setTypeOfsource(String typeOfsource) {
		this.typeOfsource = typeOfsource;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	 
}
