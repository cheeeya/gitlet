package pls;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class elton {

	public elton() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] arg) {
		File ok = new File("D:\\EltonBear\\Desktop\\folder1\\123.txt");
		File folder2 = new File("D:\\EltonBear\\Desktop\\folder2\\123.txt");
		if (ok.renameTo(folder2)) {
			System.out.println("File is moved successful!");
		} else {
			System.out.println("nah");
		}
	}

}
