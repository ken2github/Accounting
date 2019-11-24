package deprecated.reporting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ResourceNotFoundException;

import deprecated.model.books.YearBook;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.MethodInvocationException;

public class VelocityReporter implements Reportable {
	
	
	
	@Override
	public void createReport(YearBook yb, File outputDirectory) throws ReportException {
	
		String vmPath = "C:\\Users\\primo\\git\\Accounting\\src\\main\\resources\\velocity\\templates";
		Properties p = new Properties();
		p.setProperty("file.resource.loader.path", vmPath+"//");
		
		Velocity.init(p);

		VelocityContext context = new VelocityContext();

		context.put( "name", new String("Velocity") );

		Template template = null;

		try{
		  template = Velocity.getTemplate("hello-world-template.vm");
			
		}
		catch( ResourceNotFoundException rnfe ){
		  throw new ReportException("Report Exception: couldn't find the template. Detail: "+rnfe.getMessage());
		}catch( ParseErrorException pee ){
			throw new ReportException("Report Exception: syntax error: problem parsing the template. Detail: "+pee.getMessage());
		  // syntax error: problem parsing the template
		}catch( MethodInvocationException mie ){
			throw new ReportException("Report Exception: something invoked in the template threw an exception. Detail: "+mie.getMessage());
		}catch( Exception e ){
			throw new ReportException("Report Exception: unexpected error. Detail : "+e.getMessage());
		}

		StringWriter sw = new StringWriter();

		template.merge( context, sw );
		
		File outputFile = new File(outputDirectory.getAbsoluteFile()+"\\hello-world.html");
		try (PrintStream dos = new PrintStream(new FileOutputStream(outputFile))){
			dos.print(sw.toString());
		} catch (FileNotFoundException e) {
			throw new ReportException("Report Exception: couldn't find the output file. Detail: "+e.getMessage());
		}

	}

	@Override
	public String createTableReport(List<List<String>> rows, String caption, Color captionColor) throws ReportException {
		String vmPath = "C:\\Users\\primo\\git\\Accounting\\src\\main\\resources\\velocity\\templates";
		Properties p = new Properties();
		p.setProperty("file.resource.loader.path", vmPath+"//");
		
		Velocity.init(p);

		VelocityContext context = new VelocityContext();

		context.put( "rows", rows);
		context.put( "caption", caption);
		
		context.put( "captionColor", captionColor.toString());

		Template template = null;

		try{
		  template = Velocity.getTemplate("table-template.html");
			
		}
		catch( ResourceNotFoundException rnfe ){
		  throw new ReportException("Report Exception: couldn't find the template. Detail: "+rnfe.getMessage());
		}catch( ParseErrorException pee ){
			throw new ReportException("Report Exception: syntax error: problem parsing the template. Detail: "+pee.getMessage());
		  // syntax error: problem parsing the template
		}catch( MethodInvocationException mie ){
			throw new ReportException("Report Exception: something invoked in the template threw an exception. Detail: "+mie.getMessage());
		}catch( Exception e ){
			throw new ReportException("Report Exception: unexpected error. Detail : "+e.getMessage());
		}

		StringWriter sw = new StringWriter();

		template.merge( context, sw );
		
		return sw.toString();

	}

}

