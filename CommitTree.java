import java.util.HashMap;
import java.util.Iterator;
import java.io.*;
import java.nio.file.*;
//import java.nio.file.Path;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;

public class CommitTree implements Serializable{
	private int currentCommit;
	private String currentBranch;
	private HashMap<String, Commit> branches;
	public DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public CommitTree() {
		currentCommit = 0;
		branches = new HashMap<String, Commit>();
		currentBranch = "master";
		
	}
	
//	private void commitSerialWrite(Commit c){
//		try {
//			ObjectOutput output = new ObjectOutputStream(new FileOutputStream(".gitlet/commits/" + c.getID() + "/" + "commit" + c.getID() + ".txt"));
//			output.writeObject(c);
//		} catch (IOException e){
//			System.out.printf("Error: %s\n", e.toString());
//		}
//	}
	
//	private void commitSerialRead(){
//		try {
//			
//			ObjectInput input = new ObjectInputStream(new FileInputStream(".gitlet/commits/" + branches.get(currentBranch) + "/" + "commit" + branches.get(currentBranch) + ".txt"))
//			input.readObject();
//		} catch (IOException e){
//			System.out.printf("Error: %s\n", e.toString());
//		}
//	}
	
	public void newCommit(String message){
		Date date = new Date();
		Commit commit;
		if (currentCommit == 0) {
			commit = new Commit(currentCommit, message, dateFormat.format(date));
		} else {
			Commit parentCommit = branches.get(currentBranch);
			commit = new Commit(currentCommit, message, dateFormat.format(date), parentCommit);
		}
		 
		branches.put(currentBranch, commit);
		File commitDir = new File(".gitlet/commits/"+ currentCommit );
		commitDir.mkdir();
		File stagingArea = new File(".gitlet/stagingArea");
		String[] stagingFiles = stagingArea.list();
		Path targetPath = commitDir.toPath();
		for (String s: stagingFiles) {
			File file = new File(".gitlet/stagingArea/"+ s);
			Path filePath = file.toPath();
			try {
//				FileInputStream input = new FileInputStream(file);
				Files.copy(filePath, targetPath);
				Files.delete(filePath);
			} catch (FileNotFoundException e) {
				System.err.println("files not found");
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
		currentCommit++;
//		commitSerialWrite(commit);
		
	}

	
	
	
	private class Commit implements Serializable{
		private String commitMessage;
		private int commitID;
		private String commitTime;
		private Commit parentCommit;
		private ArrayList<String> committedFiles;
		
		private Commit(int id, String message, String time){
			commitID = id;
			commitMessage = message;
			commitTime = time;
			parentCommit = null;
		}
		
		private Commit(int id, String message, String time, Commit parent){
			commitID = id;
			commitMessage = message;
			commitTime = time;
			parentCommit = parent;
		}
		
		private int getID(){
			return commitID;
		}
	
	}
}
