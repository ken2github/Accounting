package engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan(basePackages = {"config"})
public class Engine {
	
	public static void main(String[] args) {
		SpringApplication.run(Engine.class, args);
	}

//	public static void main(String[] args) {
//		//check args correctness
//		
//		File srcDirectory=new File(args[0]);
//		File tmpDirectory=new File(args[1]);
//		File outputDirectory=new File(args[2]);
//		Engine.start(srcDirectory,tmpDirectory,outputDirectory);
//	}
//	
//	public static void start(File srcDirectory,File tmpDirectory,File outputDirectory) {
//		
//	}
}
