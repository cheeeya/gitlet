import java.io.*;

//import CommitTree;
//import CommitTree.Commit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;

/*	ask armani about system.out.println vs system.err.println
 * 		static final serial version UID
 * 		dangerous
 * 		copy_attributes not working??
 * 
 */

public class Gitlet {
	CommitTree cTree;

	public void testTest(String arg) {
		File orig = new File(arg);
		File rename = new File(orig.getName() + ".conflicted");
		orig.renameTo(rename);
	}

	public void initialize() {
		File gitlet = new File(".gitlet");
		if (gitlet.exists()) {
			System.out.println("A gitlet version control system already exists in the current directory.");
		} else {
			gitlet.mkdir();
			File stagingArea = new File(".gitlet/.stagingArea");
			stagingArea.mkdir();
			cTree = new CommitTree();
			File commit = new File(".gitlet/commits");
			commit.mkdir();
			cTree.newCommit("initial commit");
			commitSerialWrite(cTree);
		}
	}

	public void add(String fileName) {
		cTree = commitSerialRead();
		if (cTree.isMarked(fileName)) {
			cTree.unmarkFile(fileName);
			commitSerialWrite(cTree);
			return;
		}
		File stagingArea = new File(".gitlet/.stagingArea" + "/" + fileName);
		File toBeAdded = new File(fileName);
		File parent = new File(stagingArea.getParent());
		if (!toBeAdded.exists()) {
			System.out.println("No file in the current directory with such a name");
		} else {
			try {
				parent.mkdirs();
				Files.copy(toBeAdded.toPath(), stagingArea.toPath());
				cTree.addToVirtualStage(fileName);
				commitSerialWrite(cTree);
			} catch (IOException e) {
			}
		}
	}

	public void commit(String message) {
//		System.out.println("fuck");
		cTree = commitSerialRead();
		File stage = new File(".gitlet/.stagingArea");
		if (stage.list().length == 0 && !cTree.hasMarkedFiles()) {
			System.out.println("No changes added to the commit");
		} else {
			cTree.newCommit(message);
			commitSerialWrite(cTree);
		}
	}

	public void remove(String fileName) {
		File stage = new File(".gitlet/.stagingArea/");
		File stagedFile = new File(".gitlet/.stagingArea/" + fileName);
		File parent = stagedFile.getParentFile();
		cTree = commitSerialRead();
		if (stagedFile.exists()) {
			stagedFile.delete();
			cTree.removeFromVirtualStage(fileName);
			commitSerialWrite(cTree);
			while (!parent.equals(stage)) {
				if (parent.list().length == 0) {
					parent.delete();
				}
				parent = parent.getParentFile();
			}
		} else if (cTree.checkTrackedFiles(fileName)) {
			cTree.markFile(fileName);
			commitSerialWrite(cTree);
		} else {
			System.out.println("No reason to remove the file");
		}
	}

	private void commitSerialWrite(CommitTree c) {
		try {
			ObjectOutput output = new ObjectOutputStream(new FileOutputStream(".gitlet/commits/commitTreeSer.txt"));
			output.writeObject(c);
			output.close();
		} catch (IOException e) {
			System.out.printf("Error: %s\n", e.toString());
		}
	}

	private CommitTree commitSerialRead() {
		try {
			ObjectInput input = new ObjectInputStream(new FileInputStream(".gitlet/commits/commitTreeSer.txt"));
			CommitTree read = (CommitTree) input.readObject();
			input.close();
			return read;
		} catch (IOException e) {
			System.err.printf("Error: %s\n", e.toString());
			return null;
		} catch (ClassNotFoundException e) {
			System.err.printf("Error: %s\n", e.toString());
			return null;
		}
	}

	/*
	 * private void serialize(Object o, String filePath){ try { ObjectOutput
	 * output = new ObjectOutputStream(new FileOutputStream(filePath));
	 * output.writeObject(o); output.close(); } catch (IOException e){
	 * System.out.printf("Error: %s\n", e.toString()); } }
	 * 
	 * private Object deserialize(String filePath){ try { ObjectInput input =
	 * new ObjectInputStream(new FileInputStream(filePath)); Object file =
	 * input.readObject(); input.close(); return file; } catch (IOException e){
	 * System.err.printf("Error: %s\n", e.toString()); return null; } catch
	 * (ClassNotFoundException e2){ System.err.printf("Error: %s\n",
	 * e2.toString()); return null; } }
	 */

	public void log() {
		cTree = commitSerialRead();
		cTree.log();
	}

	public void globalLog() {
		cTree = commitSerialRead();
		cTree.globalLog();
	}

	public void find(String message) {
		cTree = commitSerialRead();
		cTree.find(message);
	}

	public void branch(String branchName) {
		cTree = commitSerialRead();
		cTree.branch(branchName);
		commitSerialWrite(cTree);
	}

	public void status() {
		cTree = commitSerialRead();
		cTree.status();
	}

	public void checkout(File fileName) {
		cTree = commitSerialRead();
		cTree.checkout(fileName);
	}

	public void checkout(int ID, String fileName) {
		cTree = commitSerialRead();
		cTree.checkout(ID, fileName);
		commitSerialWrite(cTree);
	}

	public void checkout(String branchName) {
		cTree = commitSerialRead();
		cTree.checkout(branchName);
		commitSerialWrite(cTree);
	}

	public boolean hasBranch(String branchName) {
		cTree = commitSerialRead();
		return cTree.hasBranch(branchName);
	}

	public void removeBranch(String branchName) {
		cTree = commitSerialRead();
		cTree.removeBranch(branchName);
		commitSerialWrite(cTree);
	}

	public void reset(int id) {
		cTree = commitSerialRead();
		cTree.reset(id);
		commitSerialWrite(cTree);
	}

	public void merge(String branch) throws IOException {
		cTree = commitSerialRead();
		ArrayList<String> toAdd = cTree.merge(branch);
		for (int i = 0; i < toAdd.size(); i++) {
//			System.out.println(toAdd.get(i));
			add(toAdd.get(i));
		}
//		System.out.println(cTree.hasConflict());
		if (!cTree.hasConflict()) {
//			System.out.println("commit!");
			commit("Merged " + cTree.currentBranch() + " with " + branch);
		} else {
//			System.out.println("conflict!");
			System.out.println("Encountered a merge conflict.");
		}
		commitSerialWrite(cTree);
	}

	public boolean hasConflict() {
		cTree = commitSerialRead();
		return cTree.hasConflict();
	}

	public static void main(String[] args) {
		Gitlet git = new Gitlet();
		if (args[0].equals("init")) {
			git.initialize();
			return;
		}
		List<String> conflictedCommands = Arrays.asList("add", "rm", "commit",
				"log", "global-log", "find", "status", "checkout");
		List<String> otherCommands = Arrays.asList("init", "rebase", "merge",
				"branch", "rm-branch", "reset");
		if (args.length > 0) {
			if (!conflictedCommands.contains(args[0])
					&& !otherCommands.contains(args[0])) {
				System.out.println("No command with that name exists.");
			} else {
				if (git.hasConflict() && !conflictedCommands.contains(args[0])) {
					System.out.println("Cannot do this command until the merge conflict has been resolved.");
				} else {
					if (args.length == 1) {
						mainHelpNoArg(git, args);
					} else if (args.length == 2) {
						mainHelpOneArg(git, args);
					} else if (args.length == 3) {
						mainHelpTwoArg(git, args);
					}
				}
			}
		} else {
			System.out.println("Please enter a command.");
		}
	}

	public static void mainHelpNoArg(Gitlet git, String[] args) {
		switch (args[0]) {
		case "init":
			git.initialize();
			break;
		case "log":
			git.log();
			break;
		case "global-log":
			git.globalLog();
			break;
		case "status":
			git.status();
			break;
		}
	}

	public static void mainHelpOneArg(Gitlet git, String[] args) {
		switch (args[0]) {
		case "add":
			git.add(args[1]);
			break;
		case "commit":
			git.commit(args[1]);
			break;
		case "rm":
			git.remove(args[1]);
			break;
		case "find":
			git.find(args[1]);
			break;
		case "branch":
			git.branch(args[1]);
			break;
		case "rm-branch":
			git.removeBranch(args[1]);
			break;
		case "reset":
			git.reset(Integer.parseInt(args[1]));
			break;
		case "merge":
			 try {
				git.merge(args[1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "rebase":
//			 git.rebase(args[1]);
			break;
		case "checkout":
			if (git.hasBranch(args[1])) {
				if (!git.hasConflict()) {
					git.checkout(args[1]);
				} else {
					System.out.println("Cannot do this command until the merge conflict has been resolved.");
					break;
				}
			} else {
				File fname = new File(args[1]);
				git.checkout(fname);
			}
			break;
		}
	}

	public static void mainHelpTwoArg(Gitlet git, String[] args) {
		git.checkout(Integer.parseInt(args[1]), args[2]);
	}
}
