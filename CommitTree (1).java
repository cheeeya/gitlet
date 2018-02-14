import java.util.HashMap;
import java.util.Iterator;
import java.io.*;
import java.nio.file.*;
//import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;

public class CommitTree implements Serializable {
	boolean hasConflict;
	private int currentCommit;
	private String currentBranch;
	private HashMap<String, Commit> branches;
	private ArrayList<String> markedFiles;
	private ArrayList<String> virtualStage;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public CommitTree() {
		currentCommit = 0;
		branches = new HashMap<String, Commit>();
		currentBranch = "master";
		markedFiles = new ArrayList<String>();
		virtualStage = new ArrayList<String>();
		hasConflict = false;
	}

	public void addToVirtualStage(String filename) {
		virtualStage.add(filename);
	}

	public void removeFromVirtualStage(String filename) {
		virtualStage.remove(filename);
	}

	/**
	 * Checks if a branch exists
	 * 
	 * @param s
	 *            String name of a branch
	 * @return true if the branch of name s exists
	 */
	public boolean hasBranch(String s) {
		return branches.containsKey(s);
	}

	/**
	 * Checks if any files have been added/staged
	 * 
	 * @return returns true if virtualStage.size() > 0
	 */
	public boolean hasStagedFiles() {
		return virtualStage.size() > 0;
	}

	/**
	 * Checks if a file has been marked or removal
	 * 
	 * @param filename
	 *            String name/path of a file
	 * @return true if the file has been marked for removal
	 */
	public boolean isMarked(String filename) {
		return markedFiles.contains(filename);
	}

	/**
	 * "marks" a file for removal by adding it to a list of marked files
	 * 
	 * @param filePath
	 *            String filename/path that will be removed from the upcoming
	 *            commit
	 */
	public void markFile(String filePath) {
		markedFiles.add(filePath);
	}

	/**
	 * "unmarks" a file for removal by removing it from the list of marked files
	 * 
	 * @param fileName
	 *            String filename/path of file that should be removed from the
	 *            marked files
	 */
	public void unmarkFile(String fileName) {
		markedFiles.remove(fileName);
	}

	/**
	 * Checks if any files have been marked for removal
	 * 
	 * @return true if files have been marked for removal
	 */
	public boolean hasMarkedFiles() {
		return markedFiles.size() > 0;
	}

	/**
	 * Creates a new Commit object, copies all the files from the staging area
	 * into the commit folder
	 * 
	 * @param message
	 *            String for the message that goes along with the commit
	 */
	public void newCommit(String message) {
		Date date = new Date();
		Commit commit;
		if (currentCommit == 0) {
			commit = new Commit(currentCommit, message, dateFormat.format(date));
		} else {
			Commit parentCommit = branches.get(currentBranch);
			commit = new Commit(currentCommit, message,
					dateFormat.format(date), parentCommit);
		}

		branches.put(currentBranch, commit);
		File commitDir = new File(".gitlet/commits/" + currentCommit);
		commitDir.mkdir();
		String serPath = commitDir + "/commitSer.txt";
		File stagingArea = new File(".gitlet/.stagingArea");
		File[] stagingFiles = stagingArea.listFiles();
		for (String marked : markedFiles) {
			commit.untrack(marked);
		}
		markedFiles = new ArrayList<String>();
		for (String virtualStageFiles : virtualStage) {
			commit.track(virtualStageFiles);
		}
		virtualStage = new ArrayList<String>();
		for (File f : stagingFiles) {
			File toBeAdded = new File(".gitlet/.stagingArea/" + f.getName());
			File dest = new File(".gitlet/commits/" + currentCommit + "/"
					+ f.getName());
			toBeAdded.renameTo(dest);
		}
		serialize((Object) commit, serPath);
		currentCommit++;
		if (hasConflict()) {
			falseConflictState();
		}
	}

	/**
	 * Prints out the log of all the commits associated with the current branch
	 * 
	 */
	public void log() {
		Commit current = branches.get(currentBranch);
		while (current != null) {
			System.out.println("===" + "\n" + "Commit " + current.getID()
					+ "\n" + current.getTime() + "\n" + current.getMessage()
					+ "\n");
			current = current.getParent();
		}
	}

	/**
	 * Prints a log of all commits ever made
	 * 
	 */
	public void globalLog() {
		int head = currentCommit - 1;
		// File commitFile = new File(".gitlet/commits/" + head);
		while (head >= 0) {
			String serPath = ".gitlet/commits/" + head + "/commitSer.txt";
			Commit current = (Commit) deserialize(serPath);
			System.out.println("===" + "\n" + "Commit " + current.getID()
					+ "\n" + current.getTime() + "\n" + current.getMessage()
					+ "\n");
			head -= 1;
		}
	}

	/**
	 * Finds all the commits that have the given message
	 * 
	 * @param message
	 *            String of a message associated with a commit
	 */
	public void find(String message) {
		int count = 0;
		int head = currentCommit - 1;
		// File commitFile = new File(".gitlet/commits/" + head);
		while (head >= 0) {
			String serPath = ".gitlet/commits/" + head + "/commitSer.txt";
			Commit current = (Commit) deserialize(serPath);
			if (current.getMessage().equals(message)) {
				System.out.println(current.getID());
				count += 1;
			}
			head -= 1;
		}
		if (count == 0) {
			System.out.println("Found no commit with that message.");
		}
	}

	/**
	 * Creates a new branch
	 * 
	 * @param branchName
	 *            String that the new branch will be named after
	 */
	public void branch(String branchName) {
		if (branches.containsKey(branchName)) {
			System.out.println("A branch with that name already exists.");
		} else {
			branches.put(branchName, branches.get(currentBranch));
		}
	}

	/**
	 * Prints out all the branches that currently exists, files that have been
	 * staged, and files that have been unmarked if they exist
	 * 
	 */
	public void status() {
		System.out.println("=== Branches ===");
		for (String branch : branches.keySet()) {
			if (branch.equals(currentBranch)) {
				System.out.print("*");
			}
			System.out.println(branch);
		}
		System.out.println("\n" + "=== Staged Files ===");
		for (String stagedFiles : virtualStage) {
			System.out.println(stagedFiles);
		}
		System.out.println("\n" + "=== Files Marked for Untracking ===");
		for (String s : markedFiles) {
			System.out.println(s);
		}
	}

	/**
	 * Checks if a file is currently being tracked by the head commit in the
	 * current branch
	 * 
	 * @param fileName
	 *            String of the filename/path to check
	 * @return true if the file is being tracked
	 */
	public boolean checkTrackedFiles(String fileName) {
		return branches.get(currentBranch).trackedFiles.containsKey(fileName);
	}

	/**
	 * Copies the version of a file in the head commit of the current branch
	 * into the working directory, works by calling checkout(ID, filename) on
	 * the current commit
	 * 
	 * @param fileName
	 *            name of the file in File form, used File form to differentiate
	 *            from checkout(String branch) params
	 */
	public void checkout(File fileName) {
		if (!branches.get(currentBranch).trackedFiles.keySet().contains(
				fileName.getName())) {
			System.out
					.println("File does not exist in the most recent commit, or no such branch exists.");
		} else {
			checkout(branches.get(currentBranch).getID(), fileName.getName());
		}
	}

	public void checkout(int ID, String fileName) {
		if (ID >= currentCommit) {
			System.out.println("No commit with that id exists.");
		} else {
			String commitPath = ".gitlet/commits/" + ID + "/commitSer.txt";
			Commit commit = (Commit) deserialize(commitPath);
			if (commit.trackedFiles.keySet().contains(fileName)) {
				copyToWorkingDirectory(commit, fileName);
			} else {
				System.out.println("File does not exist in that commit.");
			}
		}
	}

	public void checkout(String branchName) {
		if (branchName.equals(currentBranch)) {
			System.out.println("No need to checkout the current branch.");
		} else {
			Commit c = branches.get(branchName);
			for (String s : c.trackedFiles.keySet()) {
				copyToWorkingDirectory(c, s);
			}
			currentBranch = branchName;
		}
	}

	private void copyToWorkingDirectory(Commit c, String filepath) {
		int fileLocationID = c.trackedFiles.get(filepath).getID();
		File trackedFile = new File(filepath);
		File commitFile = new File(".gitlet/commits/" + fileLocationID + "/"
				+ filepath);
		try {
			if (trackedFile.getParent() != null) {
				File parent = new File(trackedFile.getParent());
				parent.mkdirs();
			}
			CopyOption[] options = new CopyOption[] {
					StandardCopyOption.REPLACE_EXISTING,
					StandardCopyOption.COPY_ATTRIBUTES };
			Files.copy(commitFile.toPath(), trackedFile.toPath(), options);
		} catch (IOException e) {
		}
	}

	public void removeBranch(String branchName) {
		if (branchName.equals(currentBranch)) {
			System.out.println("Cannot remove the current branch.");
		} else {
			branches.remove(branchName);
		}
	}

	public void reset(int id) {
		if (id >= currentCommit) {
			System.out.println("No commit with that ID exists");
		} else {
			String commitPath = ".gitlet/commits/" + id + "/commitSer.txt";
			Commit c = (Commit) deserialize(commitPath);
			for (String s : c.trackedFiles.keySet()) {
				copyToWorkingDirectory(c, s);
			}
			branches.put(currentBranch, c);
		}
	}
	
	public ArrayList<String> merge(String branch) {
		Commit current = branches.get(currentBranch);
		Commit given = branches.get(branch);
		Commit split = current.findSplit(given);
//		System.out.println("step one");
		ArrayList<String> currentLst = new ArrayList<String>();
		ArrayList<String> givenLst = new ArrayList<String>();
		current.filesUpTo(currentLst, split);
		given.filesUpTo(givenLst, split);
//		System.out.println("step two");
		currentLst = current.trim(currentLst, split);
		givenLst = given.trim(givenLst, split);
//		System.out.println("step three");
		ArrayList<String> toStage = new ArrayList<String>();
		File splitDir = new File(".gitlet/commits/" + split.getID());
		ArrayList<String> splitLst = new ArrayList<String>(Arrays.asList(splitDir.list()));
		for (int i = 0; i < givenLst.size(); i++) {
//			System.out.println("step four");
//			File looking = new File(givenLst.get(i));
			String looking = givenLst.get(i);
			File gLooking = new File(".gitlet/commits/" + given.getID() + "/" + looking);
//			System.out.println(looking);
			if (!currentLst.contains(looking)) {
//				System.out.println("if");
				checkout(given.getID(), looking);
				toStage.add(looking);
			} else {
//				System.out.println("else");
				File cLooking = new File(".gitlet/commits/" + current.getID() + "/" + looking);
				if (splitLst.contains(looking)) {
//					System.out.println("compare to split");
					File splitFile = new File(".gitlet/commits/" + split.getID() + "/" + looking);
					if (isFileModified(gLooking.toPath(), cLooking.toPath())) {
						if (!isFileModified(splitFile.toPath(), cLooking.toPath())) {
//							System.out.println("stage");
							checkout(given.getID(), looking);
							toStage.add(looking);
						} else {
							if (isFileModified(splitFile.toPath(), gLooking.toPath())) {
//								System.out.println("conflict");
								generateConflict(new File(looking), given);
							}
						}
					}
				} else {
//					System.out.println("compare to e/o");
					if (isFileModified(gLooking.toPath(), cLooking.toPath())) {
//						System.out.println("conflict");
						generateConflict(new File(looking), given);
					}
				}
			}
		}
		return toStage;
	}
	
	public void generateConflict(File file, Commit branch) {
		File original = new File(file.getName());
		File rename = new File(file.getName() + ".conflicted");
		System.out.println(rename);
		file.renameTo(rename);
		checkout(branch.getID(), rename.toString());
		file.renameTo(original);
	}

	private void serialize(Object o, String filePath) {
		try {
			ObjectOutput output = new ObjectOutputStream(new FileOutputStream(
					filePath));
			output.writeObject(o);
			output.close();
		} catch (IOException e) {
			System.out.printf("Error: %s\n", e.toString());
		}
	}

	private Object deserialize(String filePath) {
		try {
			ObjectInput input = new ObjectInputStream(new FileInputStream(
					filePath));
			Object file = input.readObject();
			input.close();
			return file;
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e2) {
			return null;
		}
	}

	public boolean hasConflict() {
		return hasConflict;
	}

	public void trueConflictState() {
		hasConflict = true;
	}

	public void falseConflictState() {
		hasConflict = false;
	}

	public String currentBranch() {
		return currentBranch;
	}

	public static boolean isFileModified(Path path1, Path path2) {
		try {
			byte[] file1 = Files.readAllBytes(path1);
			byte[] file2 = Files.readAllBytes(path2);
			return !Arrays.equals(file1, file2);
		} catch (IOException e) {
			return false;
		}
	}

	private class Commit implements Serializable {
		private int commitID;
		private String commitMessage;
		private String commitTime;
		private Commit parentCommit;
		private HashMap<String, Commit> trackedFiles;

		private Commit(int id, String message, String time) {
			commitID = id;
			commitMessage = message;
			commitTime = time;
			parentCommit = null;
			trackedFiles = new HashMap<String, Commit>();
		}

		private Commit(int id, String message, String time, Commit parent) {
			commitID = id;
			commitMessage = message;
			commitTime = time;
			parentCommit = parent;
			trackedFiles = new HashMap<String, Commit>();
			trackParent();
		}

		public int getID() {
			return commitID;
		}

		public String getMessage() {
			return commitMessage;
		}

		public String getTime() {
			return commitTime;
		}

		public Commit getParent() {
			return parentCommit;
		}

		private void track(String filePath) {
			trackedFiles.put(filePath, this);
		}

		private void trackParent() {
			if (parentCommit != null) {
				trackedFiles.putAll(parentCommit.trackedFiles);
			}
		}

		private void untrack(String fileName) {
			trackedFiles.remove(fileName);
		}

		public Commit findSplit(Commit given) {
			ArrayList<Commit> lst1 = new ArrayList<Commit>();
			ArrayList<Commit> lst2 = new ArrayList<Commit>();
			Commit p1 = this;
			Commit p2 = given;
			while (p1 != null && p2 != null) {
				if (p1.getID() == p2.getID()) {
					return p1;
				}
				if (lst1.contains(p2)) {
					return p2;
				}
				if (lst2.contains(p1)) {
					return p1;
				}
				lst1.add(p1);
				lst2.add(p2);
				p1 = p1.getParent();
				p2 = p2.getParent();
			}
			return null;
		}
		
		private void filesUpTo(ArrayList<String> lst, Commit until) {
//			System.out.println("wait");
			Commit p = this;
			while (p.getID() != until.getID()) {
				File curDir = new File(".gitlet/commits/" + p.getID());
//				ArrayList<String> curLst = new ArrayList<String>(Arrays.asList(curDir.list()));
				for (int i = 0; i < curDir.list().length; i++) {
					if (!curDir.list()[i].equals("commitSer.txt")) {
						if (!lst.contains(curDir.list()[i])) {
//							System.out.println(curDir.list()[i]);
							lst.add(curDir.list()[i]);
						}
					}
				}
				p = p.getParent();
			}
		}
		
		private ArrayList<String> trim(ArrayList<String> lst, Commit split) {
			ArrayList<String> result = new ArrayList<String>();
			for (String s : trackedFiles.keySet()) {
				if (lst.contains(s)) {
					result.add(s);
				}
			}
			File splitDir = new File(".gitlet/commits/" + split.getID());
			for (int i = 0; i < splitDir.list().length; i++) {
				
			}
			return result;
		}
	}
}
