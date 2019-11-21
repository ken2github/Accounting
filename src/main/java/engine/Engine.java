package engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

// @ComponentScan(basePackages = {"config"})
@SpringBootApplication
public class Engine extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Engine.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(Engine.class, args);
	}

	// public static void main(String[] args) {
	// //check args correctness
	//
	// File srcDirectory=new File(args[0]);
	// File tmpDirectory=new File(args[1]);
	// File outputDirectory=new File(args[2]);
	// Engine.start(srcDirectory,tmpDirectory,outputDirectory);
	// }
	//
	// public static void start(File srcDirectory,File tmpDirectory,File
	// outputDirectory) {
	//
	// }
}
