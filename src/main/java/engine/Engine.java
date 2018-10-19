package engine;

import java.io.File;

public class Engine {

	public static void main(String[] args) {
		//check args correctness
		
		File srcDirectory=new File(args[0]);
		File tmpDirectory=new File(args[1]);
		File outputDirectory=new File(args[2]);
		Engine.start(srcDirectory,tmpDirectory,outputDirectory);
	}
	
	public static void start(File srcDirectory,File tmpDirectory,File outputDirectory) {
		
	}
}
