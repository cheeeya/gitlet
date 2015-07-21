import java.io.*;
//import CommitTree;
//import CommitTree.Commit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class Gitlet {
	CommitTree cTree;
	
	public void initialize(){
		File gitlet = new File(".gitlet");
		if (gitlet.exists()){
			System.out.println("A gitlet version control system already exists in the current directory.");
		} else {
			gitlet.mkdir();
			File stagingArea = new File(".gitlet/.stagingArea");
			stagingArea.mkdir();
			cTree = new CommitTree();
		}
	}
	
	public void commit(String message) {
		cTree = commitSerialRead();
		File stage = new File(".gitlet/.stagingArea");
		if(stage.isDirectory()) {
			if(stage.list().length == 0){
				System.err.println("No files to be committed");
			}else{
				cTree.newCommit(message);
				commitSerialWrite(cTree);
			}	
		}
	}
	
	public void remove (String fileName){
		
	}
	
	private void commitSerialWrite(CommitTree c){
		try {
			ObjectOutput output = new ObjectOutputStream(new FileOutputStream(".gitlet/commits/commitTreeSer.txt"));
			output.writeObject(c);
			output.close();
		} catch (IOException e){
			System.out.printf("Error: %s\n", e.toString());
		}
	}
	
	private CommitTree commitSerialRead(){
		try {
			ObjectInput input = new ObjectInputStream(new FileInputStream(".gitlet/commits/commitTreeSer.txt"));
			CommitTree read = (CommitTree) input.readObject();
			input.close();
			return read;
		} catch (IOException e){
			System.err.printf("Error: %s\n", e.toString());
			return null;
		}  catch (ClassNotFoundException e){
			System.err.printf("Error: %s\n", e.toString());
			return null;
		}
	}

	public static void main(String [] args){
		Gitlet git = new Gitlet();
		String s = args[0];
		switch(s){
			case "init":
				git.initialize();
				break;
		}
	}
	
	
	
	
}
