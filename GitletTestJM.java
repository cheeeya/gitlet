import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.AfterClass;

/**
 * Class that provides JUnit tests for Gitlet, as well as a couple of utility
 * methods.
 * 
 * @author Joseph Moghadam
 * 
 *         Some code adapted from StackOverflow:
 * 
 *         http://stackoverflow.com/questions
 *         /779519/delete-files-recursively-in-java
 * 
 *         http://stackoverflow.com/questions/326390/how-to-create-a-java-string
 *         -from-the-contents-of-a-file
 * 
 *         http://stackoverflow.com/questions/1119385/junit-test-for-system-out-
 *         println
 * 
 */
public class GitletTest {
    private static final String GITLET_DIR = ".gitlet/";
    private static final String TESTING_DIR = "test_files/";
    private static final String LINE_SEPARATOR = "\r\n|[\r\n]";
    private final String workingPath = System.getProperty("user.dir");

    @Before
    public void setUp() {
        File f = new File(GITLET_DIR);
        if (f.exists()) {
            recursiveDelete(f);
        }
        f = new File(TESTING_DIR);
        if (f.exists()) {
            recursiveDelete(f);
        }
        f.mkdirs();
    }
    
    // @Test
    // public void testBasicInitialize() {
    //     System.out.println("------------test basic initialize--------------");
    //     gitlet("init");
    //     File f = new File(GITLET_DIR);
    //     assertTrue(f.exists());
    //     System.out.println("-----------------------------------------------");
    // }

    // @Test
    // public void testInitFailureCase() {
    //     System.out.println("------------test initialize failure case--------------");
    //     gitlet("init");
    //     String s1 = gitlet("init");
    //     assertEquals(s1, 
    //            "A gitlet version control system already exists in the current directory.\n");
    //     String s2 = gitlet("init");
    //     assertEquals(s2, 
    //            "A gitlet version control system already exists in the current directory.\n");
    //     System.out.println("-----------------------------------------------");
    // }

    // @Test
    // public void testBasicAdd() {
    //     System.out.println("------------test basic add--------------");
    //     String wugFileName = TESTING_DIR + "wug.txt";
    //     String wugText = "This is a wug.";
    //     createFile(wugFileName, wugText);
    //     gitlet("init");
    //     gitlet("add", wugFileName);
    //     String s = gitlet("status");
    //     //should print staged files: wug.txt
    //     System.out.println(s);
    //     System.out.println("-----------------------------------------------");
    // }


    // @Test
    // public void testBasicCheckout() {
    //     String wugFileName = TESTING_DIR + "wug.txt";
    //     String wugText = "This is a wug.";
    //     createFile(wugFileName, wugText);
    //     gitlet("init");
    //     gitlet("add", wugFileName);
    //     gitlet("commit", "added wug");
    //     writeFile(wugFileName, "This is not a wug.");
    //     gitlet("checkout", wugFileName);
    //     assertEquals(wugText, getText(wugFileName));
    // }

    /**
     * Tests that log prints out commit messages in the right order. Involves
     * init, add, commit, and log.
     */
    // @Test
    // public void testBasicLog() {
    //     gitlet("init");
    //     String commitMessage1 = "initial commit";

    //     String wugFileName = TESTING_DIR + "wug.txt";
    //     String wugText = "This is a wug.";
    //     createFile(wugFileName, wugText);
    //     gitlet("add", wugFileName);
    //     String commitMessage2 = "added wug";
    //     gitlet("commit", commitMessage2);
    // }


    @Test
    public void testAddFailureCase1() {
        System.out.println("-----------------test add failure case 1-------------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        gitlet("init");
        String s = gitlet("add", wugFileName);
        assertEquals("File does not exist.\n", s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testAddFailureCase2() {
        System.out.println("-----------------test add failure case 2-------------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        gitlet("init");
        gitlet("add", wugFileName);
        System.out.println(gitlet("status"));
        gitlet("commit", "added wug");
        System.out.println(gitlet("status"));
        String s = gitlet("add", wugFileName);
        //nothing on staged files
        System.out.println(gitlet("status"));
        assertEquals("File has not been modified since the last commit.\n", s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testAddRemovedFile() {
        System.out.println("-----------------test add removed file-------------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        gitlet("init");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        String s3 = gitlet("rm", wugFileName);
        String s1 = gitlet("status");
        gitlet("add", wugFileName);
        String s2 = gitlet("status");
        System.out.println(s3);
        //should print removed files: wug.txt
        System.out.println(s1);
        //shouldprint nothing for removed and staged files
        System.out.println(s2);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testBasicCommit() {
        System.out.println("-----------------test basic commit-------------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        gitlet("init");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        writeFile(wugFileName, "This is a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug2");
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug");
        writeFile(wugFileName, "This is not a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug2");
        writeFile(wugFileName, "This is not a wug3.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug3");
        assertEquals("This is not a wug3.", getText(wugFileName));
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testCommitFailureCase1() {
        System.out.println("-----------------test commit failure case 1-------------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        gitlet("init");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        writeFile(wugFileName, "This is a wug 2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug2");
        String s = gitlet("commit", "added wug3");
        assertEquals("No changes added to the commit.\n", s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testCommitFailureCase2() {
        System.out.println("-----------------test commit failure case 2------------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        gitlet("init");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        writeFile(wugFileName, "This is a wug 2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug2");
        String s = gitlet("commit");
        assertEquals("Please enter a commit message.\n", s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testBasicRemove() {
        System.out.println("------------test basic remove--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        gitlet("init");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        gitlet("rm", wugFileName);
        String s = gitlet("status");
        //should print removed files: wug.txt
        System.out.println(s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testRemoveFailureCase1() {
        System.out.println("------------test remove failure case 1--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        gitlet("init");
        String s = gitlet("rm", wugFileName);
        assertEquals("No reason to remove the file.\n", s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testRemoveStagedFile() {
        System.out.println("------------test remove staged file--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        gitlet("init");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        writeFile(wugFileName, "This is a wug 2.");
        gitlet("add", wugFileName);
        String s1 = gitlet("status");
        gitlet("rm", wugFileName);
        String s2 = gitlet("status");
        //should print staged files : wug.txt
        System.out.println(s1);
        //should print nothing for staged and removed files
        System.out.println(s2);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testBasicFind() {
        System.out.println("------------test basic find--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //2
        writeFile(wugFileName, "This is a wug!");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //3
        writeFile(wugFileName, "This is not a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug");
        //4d
        writeFile(wugFileName, "This is not a wug3.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //5
        writeFile(wugFileName, "This is not a wug4.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug");
        //should print 3 5
        System.out.println(gitlet("log"));
        String s = gitlet("find", "added not wug");
        System.out.println(s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testFindFailureCase() {
        System.out.println("------------test basic checkout file--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //2
        writeFile(wugFileName, "This is a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //3
        writeFile(wugFileName, "This is not a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug");
        //4
        writeFile(wugFileName, "This is not a wug3.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //5
        writeFile(wugFileName, "This is not a wug4.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug");
        String s = gitlet("find", "added hehehehe");
        assertEquals("Found no commit with that message.\n", s);
        System.out.println("-----------------------------------------------");
    }


    @Test
    public void testBasicCheckoutFile() {
        System.out.println("------------test basic checkout file--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        gitlet("init");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        writeFile(wugFileName, "This is not a wug.");
        gitlet("checkout", wugFileName);
        assertEquals(wugText, getText(wugFileName));
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testBasicCheckoutBranch() {
        System.out.println("------------test basic checkout branch--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //2
        writeFile(wugFileName, "This is a wug 2");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //create and checkout branch b1
        gitlet("branch", "b1");
        gitlet("checkout", "b1");
        //3
        writeFile(wugFileName, "This is not a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug");
        System.out.println(gitlet("status"));
        System.out.println(gitlet("global-log"));
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testBasicCheckoutBranchFileConflict() {
        System.out.println("------------test basic checkout branch file conflict--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //2
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //create and checkout branch "wug.txt"
        gitlet("branch", wugFileName);
        writeFile(wugFileName, "This is not a wug!!!");
        gitlet("checkout", wugFileName);
        System.out.println(gitlet("status"));
        assertFalse(wugText.equals(getText(wugFileName)));
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testCheckoutFailureCase1() {
        System.out.println("------------test checkout failure case 1--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        String s = gitlet("checkout", "master");
        System.out.println(gitlet("status"));
        System.out.println(s);
        assertEquals("No need to checkout the current branch.\n", s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testCheckoutFailureCase2() {
        System.out.println("------------test checkout failure case 2--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        String s = gitlet("checkout", "fuckjosh");
        assertEquals(
            "File does not exist in the most recent commit, or no such branch exists.\n", s);
        System.out.println("-----------------------------------------------");
    }
    
    @Test
    public void testCheckoutFailureCase3() {
        System.out.println("------------test checkout failure case 3--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //2
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        String s = gitlet("checkout", "3", wugFileName);
        assertEquals("No commit with that id exists.\n", s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testCheckoutFailureCase4() {
        System.out.println("------------test checkout failure case 4--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //2
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        String s = gitlet("checkout", "2", "nothing.txt");
        assertEquals("File does not exist in that commit.\n", s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testBasicBranch() {
        System.out.println("------------test basic branch--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //add branch "b1"
        gitlet("branch", "b1");
        //2
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //should print *master, b1 for branch
        String s = gitlet("status");
        System.out.println(s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testBranchFailureCase() {
        System.out.println("------------test branch failure case--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //add branch "b1"
        gitlet("branch", "b1");
        //2
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        String s = gitlet("branch", "b1");
        assertEquals("A branch with that name already exists.\n",s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testBasicRemoveBranch() {
        System.out.println("------------test basic remove branch--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //add branch "b1"
        gitlet("branch", "b1");
        //2
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        gitlet("rm-branch", "b1");
        //should have only *master branch
        String s = gitlet("status");
        System.out.println(s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testRemoveBranchFailureCase1() {
        System.out.println("------------test remove branch failure case 1-------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //add branch "b1"
        gitlet("branch", "b1");
        //2
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        String s = gitlet("rm-branch", "b2");
        assertEquals("A branch with that name does not exist.\n",s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testRemoveBranchFailureCase2() {
        System.out.println("------------test remove branch failure case 2-------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //add branch "b1"
        gitlet("branch", "b1");
        //2
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        String s = gitlet("rm-branch", "master");
        assertEquals("Cannot remove the current branch.\n",s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testBasicReset() {
        System.out.println("------------test basic reset--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //2
        writeFile(wugFileName, "This is a wug 2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug 2");
        //3
        writeFile(wugFileName, "This is a wug 3.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug 3");
        //reset to 1
        gitlet("reset", "1");
        assertEquals(wugText, getText(wugFileName));
        gitlet("reset", "2");
        assertEquals("This is a wug 2.", getText(wugFileName));
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testBasicReset2() {

        /*
            0 -- 1  -- 2 -- 3  master modifies wug1
                       |
                       4       b1 modifies wug2
        */

        System.out.println("------------test basic reset 2--------------");
        String wugFileName1 = TESTING_DIR + "wug1.txt";
        String wugText1 = "This is a wug1.";
        createFile(wugFileName1, wugText1);
        String wugFileName2 = TESTING_DIR + "wug2.txt";
        String wugText2 = "This is a wug2.";
        createFile(wugFileName2, wugText2);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName1);
        gitlet("add", wugFileName2);
        gitlet("commit", "added wugs");
        //2
        writeFile(wugFileName1, "This is a wug1 2.");
        writeFile(wugFileName2, "This is a wug2 2.");
        gitlet("add", wugFileName1);
        gitlet("add", wugFileName2);
        gitlet("commit", "added wugs 2");
        //create b1
        gitlet("branch", "b1");
        //3
        writeFile(wugFileName1, "This is a wug1 3.");
        gitlet("add", wugFileName1);
        gitlet("commit", "added wug1 3");
        //checkout to b1
        gitlet("checkout", "b1");
        //4
        writeFile(wugFileName2, "This is a wug2 3.");
        gitlet("add", wugFileName2);
        gitlet("commit", "added wug2 3");
        //reset to 1
        gitlet("reset", "1");
        //should stay in b1
        System.out.println(gitlet("status"));
        //should print 1 0
        System.out.println(gitlet("log"));
        //files are origin pieces
        assertEquals(wugText1, getText(wugFileName1));
        assertEquals(wugText2, getText(wugFileName2));
        //reset to 3
        gitlet("reset", "3");
        //should stay in b1
        System.out.println(gitlet("status"));
        //should print 3 2 1 0
        System.out.println(gitlet("log"));
        //file 1 is wug1 3, file 2 is wug 2 2;
        assertEquals("This is a wug2 2.", getText(wugFileName2));
        assertEquals("This is a wug1 3.", getText(wugFileName1));

        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testResetFailureCase() {
        System.out.println("------------test reset failure case-------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //2
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        String s = gitlet("reset", "3");
        assertEquals("No commit with that id exists.\n",s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testBasicLog() {
        /*
        
        0 -- 1 -- 2 -- 3 -- 4 -- 5* master
        */
        System.out.println("------------test basic log--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //2
        writeFile(wugFileName, "This is a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug2");
        //3
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug");
        //4
        writeFile(wugFileName, "This is not a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug2");
        //5
        writeFile(wugFileName, "This is not a wug3.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug3");
        String s = gitlet("log");
        //should print series of commits 5 4 3 2 1 0
        System.out.println(s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testBasicGlobalLog() {
        /*
        
        0 -- 1 -- 2 -- 3 -- 4 -- 5* master
        */
        System.out.println("------------test basic global log--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //2
        writeFile(wugFileName, "This is a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug2");
        //3
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug");
        //4
        writeFile(wugFileName, "This is not a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug2");
        //5
        writeFile(wugFileName, "This is not a wug3.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug3");
        String s = gitlet("global-log");
        //should print series of commits 5 4 3 2 1 0
        System.out.println(s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testLog2() {
        /*
        
        0 -- 1 -- 2 -- 3 -- 4 master
                  |
                  5 -- 6* b1
        */

        System.out.println("------------test log 2--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //2
        writeFile(wugFileName, "This is a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug2");
        //add b1 branch
        gitlet("branch", "b1");
        //3
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug");
        //4
        writeFile(wugFileName, "This is not a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug2");
        //checkout to b1
        gitlet("checkout", "b1");
        //5
        writeFile(wugFileName, "This is not a wug3.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug3");
        //6
        writeFile(wugFileName, "This is not a wug4.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug4");
        System.out.println(gitlet("status"));
        String s = gitlet("log");
        //should print series of commits 6 5 2 1 0
        System.out.println(s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testGlobalLog2() {
        /*
        
        0 -- 1 -- 2 -- 3 -- 4 master
                  |
                  5 -- 6* b1
        */

        System.out.println("------------test global log 2--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //2
        writeFile(wugFileName, "This is a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug2");
        //add b1 branch
        gitlet("branch", "b1");
        //3
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug");
        //4
        writeFile(wugFileName, "This is not a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug2");
        //checkout to b1
        gitlet("checkout", "b1");
        //5
        writeFile(wugFileName, "This is not a wug3.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug3");
        //6
        writeFile(wugFileName, "This is not a wug4.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug4");
        //should print series of commits 6 5 4 3 2 1 0
        String s = gitlet("global-log");
        System.out.println(s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testLog3() {
        /*
                       7* -- 8 b2
                       |
        0 -- 1 -- 2 -- 3 -- 4 master
                  |
                  5 -- 6 b1
        */

        System.out.println("------------test log 3--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //2
        writeFile(wugFileName, "This is a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug2");
        //add b1 branch
        gitlet("branch", "b1");
        //3
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug");
        //4
        writeFile(wugFileName, "This is not a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug2");
        //checkout to b1
        gitlet("checkout", "b1");
        //5
        writeFile(wugFileName, "This is not a wug3.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug3");
        //6
        writeFile(wugFileName, "This is not a wug4.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug4");
        //checkout to master
        gitlet("checkout", "master");
        //reset to commit 3
        gitlet("reset", "3");
        //make and checkout branch b2
        gitlet("branch","b2");
        gitlet("checkout","b2");
        //7
        writeFile(wugFileName, "This is not a wug5.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug5");
        //8
        writeFile(wugFileName, "This is not a wug6.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug6");
        //reset to commit 7
        gitlet("reset", "7");
        //should print series of commits 7 3 2 1 0
        String s = gitlet("log");
        System.out.println(s);
        System.out.println("-----------------------------------------------");
    }


    @Test
    public void testLog4() {
        /*
                       7* -- 8 b2
                       |
        0 -- 1 -- 2 -- 3 -- 4 master (removed)
                  |
                  5 -- 6 b1
        */

        System.out.println("------------test log 4--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //2
        writeFile(wugFileName, "This is a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug2");
        //add b1 branch
        gitlet("branch", "b1");
        //3
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug");
        //4
        writeFile(wugFileName, "This is not a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug2");
        //checkout to b1
        gitlet("checkout", "b1");
        //5
        writeFile(wugFileName, "This is not a wug3.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug3");
        //6
        writeFile(wugFileName, "This is not a wug4.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug4");
        //checkout to master
        gitlet("checkout", "master");
        //reset to commit 3
        gitlet("reset", "3");
        //make and checkout branch b2
        gitlet("branch","b2");
        gitlet("checkout","b2");
        //7
        writeFile(wugFileName, "This is not a wug5.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug5");
        //8
        writeFile(wugFileName, "This is not a wug6.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug6");
        //reset to commit 7
        gitlet("reset", "7");
        //remove master
        gitlet("rm-branch", "master");
        //should print series of commits 7 3 2 1 0
        String s = gitlet("log");
        System.out.println(s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testLog5() {
        
        //                7 -- 8 b2
        //                |
        // 0 -- 1 -- 2 -- 3 -- 4 master (removed)
        //           |
        //           5 -- 6 b1
        //                |
        //                9* -- 10 b3

        

        System.out.println("------------test log 5--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //2
        writeFile(wugFileName, "This is a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug2");
        //add b1 branch
        gitlet("branch", "b1");
        //3
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug");
        //4
        writeFile(wugFileName, "This is not a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug2");
        //checkout to b1
        gitlet("checkout", "b1");
        //5
        writeFile(wugFileName, "This is not a wug3.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug3");
        //6
        writeFile(wugFileName, "This is not a wug4.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug4");
        //checkout to master
        gitlet("checkout", "master");
        //reset to commit 3
        gitlet("reset", "3");
        //make and checkout branch b2
        gitlet("branch","b2");
        gitlet("checkout","b2");
        //7
        writeFile(wugFileName, "This is not a wug5.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug5");
        //8
        writeFile(wugFileName, "This is not a wug6.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug6");
        //reset to commit 7
        gitlet("reset", "7");
        //remove master
        gitlet("rm-branch", "master");
        //checkout to b1
        gitlet("checkout", "b1");
        //make and checkout to branch b3
        gitlet("branch", "b3");
        gitlet("checkout", "b3");
        //9
        writeFile(wugFileName, "This is not a wug7.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug7");
        //10
        writeFile(wugFileName, "This is not a wug8.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug8");
        //reset to commit 9
        gitlet("reset", "9");
        //should print series of commits 9 6 5 2 1 0
        String s = gitlet("log");
        System.out.println(s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testLog6() {
        /*
                       7 -- 8 b2 --------------------
                       |                            |
        0 -- 1 -- 2 -- 3 -- 4 master (removed)      |
                  |                                 |  merge
                  5 -- 6 b1                         | 
                       |                            v
                       9* -- 10 b3-------------------
        */

        System.out.println("------------test log 6--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //2
        writeFile(wugFileName, "This is a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug2");
        //add b1 branch
        gitlet("branch", "b1");
        //3
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug");
        //4
        writeFile(wugFileName, "This is not a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug2");
        //checkout to b1
        gitlet("checkout", "b1");
        //5
        writeFile(wugFileName, "This is not a wug3.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug3");
        //6
        writeFile(wugFileName, "This is not a wug4.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug4");
        
        //checkout to master
        gitlet("checkout", "master");
        //reset to commit 3
        gitlet("reset", "3");
        //make and checkout branch b2
        gitlet("branch","b2");
        gitlet("checkout","b2");
        //7
        writeFile(wugFileName, "This is not a wug5.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug5");
        //8
        writeFile(wugFileName, "This is not a wug6.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug6");
        //reset to commit 7
        gitlet("reset", "7");
        //remove master
        gitlet("rm-branch", "master");
        //checkout to b1
        gitlet("checkout", "b1");
        //make and checkout to branch b3
        gitlet("branch", "b3");
        gitlet("checkout", "b3");
        //9
        writeFile(wugFileName, "This is not a wug7.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug7");
        //10
        writeFile(wugFileName, "This is not a wug8.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug8");
        //reset to commit 9
        gitlet("reset", "9");
        //merge b3, b2
        System.out.println(gitlet("merge", "b2"));
        //should print series of commits 9 6 5 2 1 0
        String s = gitlet("log");
        System.out.println(s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testLog7() {

        /*
                       7 -- 8(x) b2 -------------------- 11(5) -- 12(6) -- 13(9)* b3
                       |                            ^
        0 -- 1 -- 2 -- 3 -- 4 master (removed)      |
                  |                                 |  rebase
                  5 -- 6 b1                         | 
                       |                            |
                       9* -- 10 b3 (removed) ---------
        */

        System.out.println("------------test log 7--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "1");
        //2
        writeFile(wugFileName, "This is a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "2");
        //add b1 branch
        gitlet("branch", "b1");
        //3
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "3");
        //4
        writeFile(wugFileName, "This is not a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "4");
        //checkout to b1
        gitlet("checkout", "b1");
        //5
        writeFile(wugFileName, "This is not a wug3.");
        gitlet("add", wugFileName);
        gitlet("commit", "5");
        //6
        writeFile(wugFileName, "This is not a wug4.");
        gitlet("add", wugFileName);
        gitlet("commit", "6");
        //checkout to master
        gitlet("checkout", "master");
        //reset to commit 3
        gitlet("reset", "3");
        //make and checkout branch b2
        gitlet("branch","b2");
        gitlet("checkout","b2");
        //7
        writeFile(wugFileName, "This is not a wug5.");
        gitlet("add", wugFileName);
        gitlet("commit", "7");
        //8
        writeFile(wugFileName, "This is not a wug6.");
        gitlet("add", wugFileName);
        gitlet("commit", "8");
        //reset to commit 7
        gitlet("reset", "7");
        //remove master
        gitlet("rm-branch", "master");
        //checkout to b1
        gitlet("checkout", "b1");
        //make and checkout to branch b3
        gitlet("branch", "b3");
        gitlet("checkout", "b3");
        //9
        writeFile(wugFileName, "This is not a wug7.");
        gitlet("add", wugFileName);
        gitlet("commit", "9");
        //10
        writeFile(wugFileName, "This is not a wug8.");
        gitlet("add", wugFileName);
        gitlet("commit", "10");
        // System.out.println(gitlet("status"));
        //reset to commit 9
        gitlet("reset", "9");
        //rebase b3, b2
        System.out.println(gitlet("rebase", "b2"));
        //should print series of commits 13 12 11 7 3 2 1 0,
        //with commit messages 9 6 5 7 3 2 1 "initial commit"
        String s = gitlet("log");
        System.out.println(s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testGolbalLog7() {
        
        /*
                       7 -- 8 b2 -------------------- 11(5) -- 12(6) -- 13(9)* b3
                       |                            ^
        0 -- 1 -- 2 -- 3 -- 4 master (removed)      |
                  |                                 |  rebase
                  5 -- 6 b1                         | 
                       |                            |
                       9* -- 10 b3 (removed) ---------
        */

        System.out.println("------------test global log 7--------------");
        String wugFileName = TESTING_DIR + "wug.txt";
        String wugText = "This is a wug.";
        createFile(wugFileName, wugText);
        //0
        gitlet("init");
        //1
        gitlet("add", wugFileName);
        gitlet("commit", "added wug");
        //2
        writeFile(wugFileName, "This is a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added wug2");
        //add b1 branch
        gitlet("branch", "b1");
        //3
        writeFile(wugFileName, "This is not a wug.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug");
        //4
        writeFile(wugFileName, "This is not a wug2.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug2");
        //checkout to b1
        gitlet("checkout", "b1");
        //5
        writeFile(wugFileName, "This is not a wug3.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug3");
        //6
        writeFile(wugFileName, "This is not a wug4.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug4");
        
        //checkout to master
        gitlet("checkout", "master");
        //reset to commit 3
        gitlet("reset", "3");
        //make and checkout branch b2
        gitlet("branch","b2");
        gitlet("checkout","b2");
        //7
        writeFile(wugFileName, "This is not a wug5.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug5");
        //8
        writeFile(wugFileName, "This is not a wug6.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug6");
        //reset to commit 7
        gitlet("reset", "7");
        //remove master
        gitlet("rm-branch", "master");
        //checkout to b1
        gitlet("checkout", "b1");
        //make and checkout to branch b3
        gitlet("branch", "b3");
        gitlet("checkout", "b3");
        //9
        writeFile(wugFileName, "This is not a wug7.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug7");
        //10
        writeFile(wugFileName, "This is not a wug8.");
        gitlet("add", wugFileName);
        gitlet("commit", "added not wug8");
        //reset to commit 9
        gitlet("reset", "9");
        //merge b3, b2
        gitlet("rebase", "b2");
        //should print series of commits 13 12 11 10 9 8 7 6 5 4 3 2 1 0
        String s = gitlet("global-log");
        System.out.println(s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testBasicMerge1() {

        /*
        0 -- 1 -- 2     master
             |
             3 -- 4*    b1
        */

        System.out.println("------------test merge with modified given branch--------------");
        //wug1 file
        String wug1FileName = TESTING_DIR + "wug1.txt";
        String wug1Text = "This is a wug 1.";
        createFile(wug1FileName, wug1Text);
        //wug2 file
        String wug2FileName = TESTING_DIR + "wug2.txt";
        String wug2Text = "This is a wug 2.";
        createFile(wug2FileName, wug2Text);
        //0
        gitlet("init");
        //1
        gitlet("add", wug1FileName);
        gitlet("add", wug2FileName);
        gitlet("commit", "commit 1");
        //create b1
        gitlet("branch", "b1");
        //2
        writeFile(wug1FileName, "This is a modified wug 1.");
        gitlet("add", wug1FileName);
        gitlet("commit", "rewrite wug1 in master");
        //checkout to b1
        gitlet("checkout", "b1");
        //3
        writeFile(wug2FileName, "This is a modified wug 2.");
        gitlet("add", wug2FileName);
        gitlet("commit", "rewrite wug2 in b1");
        //4 
        writeFile(wug2FileName, "This is a further modified wug 2.");
        gitlet("add", wug2FileName);
        gitlet("commit", "rewrite wug2 in b1");
        //merge master to b1
        gitlet("merge", "master");
        //should print 4 3 1 0
        System.out.println(gitlet("log"));
        //only given branch modified file is merged into the commit
        assertEquals("This is a modified wug 1.", getText(wug1FileName));
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testBasicMerge2() {

        /*
        0 -- 1 -- 2     master
             |
             3 -- 4*    b1
        */

        System.out.println("------------test merge with modified current branch--------------");
        //wug1 file
        String wug1FileName = TESTING_DIR + "wug1.txt";
        String wug1Text = "This is a wug 1.";
        createFile(wug1FileName, wug1Text);
        //wug2 file
        String wug2FileName = TESTING_DIR + "wug2.txt";
        String wug2Text = "This is a wug 2.";
        createFile(wug2FileName, wug2Text);
        //0
        gitlet("init");
        //1
        gitlet("add", wug1FileName);
        gitlet("add", wug2FileName);
        gitlet("commit", "commit 1");
        //create b1
        gitlet("branch", "b1");
        //2
        writeFile(wug1FileName, "This is a modified wug 1.");
        gitlet("add", wug1FileName);
        gitlet("commit", "rewrite wug1 in master");
        //checkout to b1
        gitlet("checkout", "b1");
        //3
        writeFile(wug2FileName, "This is a modified wug 2.");
        gitlet("add", wug2FileName);
        gitlet("commit", "rewrite wug2 in b1");
        //4 
        writeFile(wug2FileName, "This is a further modified wug 2.");
        gitlet("add", wug2FileName);
        gitlet("commit", "rewrite wug2 in b1");
        //merge master to b1
        gitlet("merge", "master");
        //should print 4 3 1 0
        System.out.println(gitlet("log"));
        //only current branch modified file does not change
        assertEquals("This is a further modified wug 2.", getText(wug2FileName));
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testBasicMerge3() {

        /*
        0 -- 1 -- 2     master
             |
             3 -- 4*    b1
        */

        System.out.println("------------test merge with both modified files--------------");
        //wug1 file
        String wug1FileName = TESTING_DIR + "wug1.txt";
        String wug1Text = "This is a wug 1.";
        createFile(wug1FileName, wug1Text);
        //wug2 file
        String wug2FileName = TESTING_DIR + "wug2.txt";
        String wug2Text = "This is a wug 2.";
        createFile(wug2FileName, wug2Text);
        //wug3 file
        String wug3FileName = TESTING_DIR + "wug3.txt";
        String wug3Text = "This is a wug 3.";
        createFile(wug3FileName, wug3Text);
        //0
        gitlet("init");
        //1
        gitlet("add", wug1FileName);
        gitlet("add", wug2FileName);
        gitlet("commit", "commit 1");
        // System.out.println(gitlet("status"));
        // System.out.println(gitlet("log"));
        //create b1
        gitlet("branch", "b1");
        //2
        writeFile(wug1FileName, "This is a modified wug 1.");
        writeFile(wug2FileName, "This is a uselessly modified wug 2.");
        gitlet("add", wug1FileName);
        gitlet("add", wug2FileName);
        // System.out.println(gitlet("status"));
        gitlet("commit", "rewrite wug1, wug2 in master");
        // System.out.println(gitlet("status"));
        // System.out.println(gitlet("log"));
        //checkout to b1
        gitlet("checkout", "b1");
        //3
        writeFile(wug2FileName, "This is a modified wug 2.");
        gitlet("add", wug2FileName);
        gitlet("commit", "rewrite wug2 in b1");
        // System.out.println(gitlet("status"));
        // System.out.println(gitlet("log"));
        //4 
        writeFile(wug2FileName, "This is a further modified wug 2.");
        gitlet("add", wug2FileName);
        gitlet("add", wug3FileName);
        
        gitlet("commit", "rewrite wug2 in b1");
        System.out.println(gitlet("status"));
        // System.out.println(gitlet("log"));
        // System.out.println(gitlet("status"));
        //merge master to b1
        System.out.println(gitlet("merge", "master"));
        //should print 4 3 1 0
        // System.out.println(gitlet("log"));
        //if file both exist, remain current one, create .conflicted file
        assertEquals("This is a further modified wug 2.", getText(wug2FileName));
        assertTrue((new File(workingPath + "/" + wug2FileName + ".conflicted")).exists());
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testMergeFailureCase1() {
        System.out.println("------------test merge failure case 1--------------");
        //wug1 file
        String wug1FileName = TESTING_DIR + "wug1.txt";
        String wug1Text = "This is a wug 1.";
        createFile(wug1FileName, wug1Text);
        //wug2 file
        String wug2FileName = TESTING_DIR + "wug2.txt";
        String wug2Text = "This is a wug 2.";
        createFile(wug2FileName, wug2Text);
        //0
        gitlet("init");
        //1
        gitlet("add", wug1FileName);
        gitlet("add", wug2FileName);
        gitlet("commit", "commit 1");
        //create b1
        gitlet("branch", "b1");
        String s = gitlet("merge", "b2");
        assertEquals("A branch with that name does not exist.\n", s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testMergeFailureCase2() {
        System.out.println("------------test merge failure case 2--------------");
        //wug1 file
        String wug1FileName = TESTING_DIR + "wug1.txt";
        String wug1Text = "This is a wug 1.";
        createFile(wug1FileName, wug1Text);
        //wug2 file
        String wug2FileName = TESTING_DIR + "wug2.txt";
        String wug2Text = "This is a wug 2.";
        createFile(wug2FileName, wug2Text);
        //0
        gitlet("init");
        //1
        gitlet("add", wug1FileName);
        gitlet("add", wug2FileName);
        gitlet("commit", "commit 1");
        //create and checkout b1
        gitlet("branch", "b1");
        gitlet("checkout", "b1");
        String s = gitlet("merge", "b1");
        assertEquals("Cannot merge a branch with itself.\n", s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testBasicRebase1() {

        /*
        0 -- 1 -- 2    master -- 5(3) -- 6(4)* b1
             |
             3 -- 4    b1 (removed)
        */

        System.out.println("------------test rebase with modified given branch--------------");
        //wug1 file
        String wug1FileName = TESTING_DIR + "wug1.txt";
        String wug1Text = "This is a wug 1.";
        createFile(wug1FileName, wug1Text);
        //wug2 file
        String wug2FileName = TESTING_DIR + "wug2.txt";
        String wug2Text = "This is a wug 2.";
        createFile(wug2FileName, wug2Text);
        //0
        gitlet("init");
        //1
        gitlet("add", wug1FileName);
        gitlet("add", wug2FileName);
        gitlet("commit", "commit 1");
        //create b1
        gitlet("branch", "b1");
        //2
        writeFile(wug1FileName, "This is a modified wug 1.");
        gitlet("add", wug1FileName);
        gitlet("commit", "rewrite wug1 in master");
        //checkout to b1
        gitlet("checkout", "b1");
        //3
        writeFile(wug2FileName, "This is a modified wug 2.");
        gitlet("add", wug2FileName);
        gitlet("commit", "rewrite wug2 in b1");
        //4 
        writeFile(wug2FileName, "This is a further modified wug 2.");
        gitlet("add", wug2FileName);
        gitlet("commit", "rewrite wug2 in b1");
        //rebase b1 to master
        gitlet("rebase", "master");
        //should print 6 5 2 1 0
        System.out.println(gitlet("log"));
        //only given branch modified file is replaced
        assertEquals("This is a modified wug 1.", getText(wug1FileName));
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testBasicRebase2() {

        /*
        0 -- 1 -- 2    master -- 5(3) -- 6(4)* b1
             |
             3 -- 4    b1 (removed)
        */

        System.out.println("------------test rebase with modified current branch--------------");
        //wug1 file
        String wug1FileName = TESTING_DIR + "wug1.txt";
        String wug1Text = "This is a wug 1.";
        createFile(wug1FileName, wug1Text);
        //wug2 file
        String wug2FileName = TESTING_DIR + "wug2.txt";
        String wug2Text = "This is a wug 2.";
        createFile(wug2FileName, wug2Text);
        //0
        gitlet("init");
        //1
        gitlet("add", wug1FileName);
        gitlet("add", wug2FileName);
        gitlet("commit", "commit 1");
        //create b1
        gitlet("branch", "b1");
        //2
        writeFile(wug1FileName, "This is a modified wug 1.");
        gitlet("add", wug1FileName);
        gitlet("commit", "rewrite wug1 in master");
        //checkout to b1
        gitlet("checkout", "b1");
        //3
        writeFile(wug2FileName, "This is a modified wug 2.");
        gitlet("add", wug2FileName);
        gitlet("commit", "rewrite wug2 in b1");
        //4 
        writeFile(wug2FileName, "This is a further modified wug 2.");
        gitlet("add", wug2FileName);
        gitlet("commit", "rewrite wug2 in b1");
        //rebase b1 to master
        gitlet("rebase", "master");
        //should print 6 5 2 1 0
        System.out.println(gitlet("log"));
        //reset to current head
        gitlet("reset", "6");
        //only current branch modified file should not be replaced
        assertEquals("This is a further modified wug 2.", getText(wug2FileName));
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testBasicRebase3() {

        /*
        0 -- 1 -- 2    master -- 5(3) -- 6(4)* b1
             |
             3 -- 4    b1 (removed)
        */

        System.out.println("------------test rebase with conflicted file--------------");
        //wug1 file
        String wug1FileName = TESTING_DIR + "wug1.txt";
        String wug1Text = "This is a wug 1.";
        createFile(wug1FileName, wug1Text);
        //wug2 file
        String wug2FileName = TESTING_DIR + "wug2.txt";
        String wug2Text = "This is a wug 2.";
        createFile(wug2FileName, wug2Text);
        //0
        gitlet("init");
        //1
        gitlet("add", wug1FileName);
        gitlet("add", wug2FileName);
        gitlet("commit", "commit 1");
        //create b1
        gitlet("branch", "b1");
        //2
        writeFile(wug1FileName, "This is a modified wug 1.");
        writeFile(wug2FileName, "This is a uselessly modified wug 2.");
        gitlet("add", wug1FileName);
        gitlet("add", wug2FileName);
        gitlet("commit", "rewrite wug1, wug2 in master");
        //checkout to b1
        gitlet("checkout", "b1");
        //3
        writeFile(wug2FileName, "This is a modified wug 2.");
        gitlet("add", wug2FileName);
        gitlet("commit", "rewrite wug2 in b1");
        //4 
        writeFile(wug2FileName, "This is a further modified wug 2.");
        gitlet("add", wug2FileName);
        gitlet("commit", "rewrite wug2 in b1");
        //rebase b1 to master
        gitlet("rebase", "master");
        //should print 6 5 2 1 0
        System.out.println(gitlet("log"));
        //for conflicted files, use current branch version
        assertEquals("This is a further modified wug 2.", getText(wug2FileName));
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testRebaseFailureCase1() {
        System.out.println("------------test rebase failure case 1--------------");
        //wug1 file
        String wug1FileName = TESTING_DIR + "wug1.txt";
        String wug1Text = "This is a wug 1.";
        createFile(wug1FileName, wug1Text);
        //wug2 file
        String wug2FileName = TESTING_DIR + "wug2.txt";
        String wug2Text = "This is a wug 2.";
        createFile(wug2FileName, wug2Text);
        //0
        gitlet("init");
        //1
        gitlet("add", wug1FileName);
        gitlet("add", wug2FileName);
        gitlet("commit", "commit 1");
        //create and checkout b1
        gitlet("branch", "b1");
        gitlet("checkout", "b1");
        String s = gitlet("rebase", "b5");
        assertEquals("A branch with that name does not exist.\n", s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testRebaseFailureCase2() {
        System.out.println("------------test rebase failure case 2--------------");
        //wug1 file
        String wug1FileName = TESTING_DIR + "wug1.txt";
        String wug1Text = "This is a wug 1.";
        createFile(wug1FileName, wug1Text);
        //wug2 file
        String wug2FileName = TESTING_DIR + "wug2.txt";
        String wug2Text = "This is a wug 2.";
        createFile(wug2FileName, wug2Text);
        //0
        gitlet("init");
        //1
        gitlet("add", wug1FileName);
        gitlet("add", wug2FileName);
        gitlet("commit", "commit 1");
        //create and checkout b1
        gitlet("branch", "b1");
        gitlet("checkout", "b1");
        String s = gitlet("rebase", "b1");
        assertEquals("Cannot rebase a branch onto itself.\n", s);
        System.out.println("-----------------------------------------------");
    }

    @Test
    public void testRebaseFailureCase3() {
        /*
        0 -- 1 master -- 2 b1
        */
        System.out.println("------------test rebase failure case 3--------------");
        //wug1 file
        String wug1FileName = TESTING_DIR + "wug1.txt";
        String wug1Text = "This is a wug 1.";
        createFile(wug1FileName, wug1Text);
        //wug2 file
        String wug2FileName = TESTING_DIR + "wug2.txt";
        String wug2Text = "This is a wug 2.";
        createFile(wug2FileName, wug2Text);
        //0
        gitlet("init");
        //1
        gitlet("add", wug1FileName);
        gitlet("add", wug2FileName);
        gitlet("commit", "commit 1");
        //create and checkout b1
        gitlet("branch", "b1");
        gitlet("checkout", "b1");
        //2
        writeFile(wug1FileName, "This is a modified wug 1.");
        writeFile(wug2FileName, "This is a modified wug 2.");
        gitlet("add", wug1FileName);
        gitlet("add", wug2FileName);
        gitlet("commit", "commit 1");
        String s = gitlet("rebase", "master");
        assertEquals("Already up-to-date.\n", s);
        System.out.println("-----------------------------------------------");
    }

    private static void setUpInteractiveRebase() {
        /*
        0 -- 1 -- 2        master
             |
             3 -- 4 -- 5   b1
        */

        System.out.println("------------Currently Setting up for i-rebase --------------");
        //wug1 file
        String wug1FileName = TESTING_DIR + "wug1.txt";
        String wug1Text = "This is a wug 1.";
        createFile(wug1FileName, wug1Text);
        //wug2 file
        String wug2FileName = TESTING_DIR + "wug2.txt";
        String wug2Text = "This is a wug 2.";
        createFile(wug2FileName, wug2Text);
        //0
        gitlet("init");
        //1
        gitlet("add", wug1FileName);
        gitlet("add", wug2FileName);
        gitlet("commit", "commit 1: add everything");
        //create b1
        gitlet("branch", "b1");
        //2
        writeFile(wug1FileName, "This is a modified wug 1.");
        writeFile(wug2FileName, "This is a uselessly modified wug 2.");
        gitlet("add", wug1FileName);
        gitlet("add", wug2FileName);
        gitlet("commit", "commit 2: rewrite wug1, wug2 in master");
        //checkout to b1
        gitlet("checkout", "b1");
        //3
        writeFile(wug2FileName, "This is a modified wug 2.");
        gitlet("add", wug2FileName);
        gitlet("commit", "commit 3: rewrite wug2 in b1");
        //4 
        writeFile(wug2FileName, "This is a further modified wug 2.");
        gitlet("add", wug2FileName);
        gitlet("commit", "commit 4: rewrite wug2 in b1");
        //5
        writeFile(wug2FileName, "This is a super modified wug 2.");
        gitlet("add", wug2FileName);
        gitlet("commit", "commit 5: rewrite wug2 in b1");
        System.out.println("-----------------------------------------------");
    }

    // @AfterClass
    // public static void interactiveRebaseTest() {
        

    //     0 -- 1 -- 2        master  ---- 6(3) (-- 7(4) )-- 8(5) b1
    //          |                               (can skip)
    //          3 -- 4 -- 5   b1(removed)
            
        

    //     setUpInteractiveRebase();
    //     //type everything you want to check in terminal...
    // }



    /**
     * Convenience method for calling Gitlet's main. Anything that is printed
     * out during this call to main will NOT actually be printed out, but will
     * instead be returned as a string from this method.
     * 
     * Prepares a 'yes' answer on System.in so as to automatically pass through
     * dangerous commands.
     * 
     * The '...' syntax allows you to pass in an arbitrary number of String
     * arguments, which are packaged into a String[].
     */

    private static String gitlet(String... args) {
        PrintStream originalOut = System.out;
        InputStream originalIn = System.in;
        ByteArrayOutputStream printingResults = new ByteArrayOutputStream();
        try {
            /*
             * Below we change System.out, so that when you call
             * System.out.println(), it won't print to the screen, but will
             * instead be added to the printingResults object.
             */
            System.setOut(new PrintStream(printingResults));

            /*
             * Prepares the answer "yes" on System.In, to pretend as if a user
             * will type "yes". You won't be able to take user input during this
             * time.
             */
            String answer = "yes";
            InputStream is = new ByteArrayInputStream(answer.getBytes());
            System.setIn(is);

            /* Calls the main method using the input arguments. */
            Gitlet.main(args);

        } finally {
            /*
             * Restores System.out and System.in (So you can print normally and
             * take user input normally again).
             */
            System.setOut(originalOut);
            System.setIn(originalIn);
        }
        return printingResults.toString();
    }

    /**
     * Returns the text from a standard text file (won't work with special
     * characters).
     */
    private static String getText(String fileName) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(fileName));
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Creates a new file with the given fileName and gives it the text
     * fileText.
     */
    private static void createFile(String fileName, String fileText) {
        File f = new File(fileName);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writeFile(fileName, fileText);
    }

    /**
     * Replaces all text in the existing file with the given text.
     */
    private static void writeFile(String fileName, String fileText) {
        FileWriter fw = null;
        try {
            File f = new File(fileName);
            fw = new FileWriter(f, false);
            fw.write(fileText);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Deletes the file and all files inside it, if it is a directory.
     */
    private static void recursiveDelete(File d) {
        if (d.isDirectory()) {
            for (File f : d.listFiles()) {
                recursiveDelete(f);
            }
        }
        d.delete();
    }

    /**
     * Returns an array of commit messages associated with what log has printed
     * out.
     */
    private static String[] extractCommitMessages(String logOutput) {
        String[] logChunks = logOutput.split("====");
        int numMessages = logChunks.length - 1;
        String[] messages = new String[numMessages];
        for (int i = 0; i < numMessages; i++) {
            System.out.println(logChunks[i + 1]);
            String[] logLines = logChunks[i + 1].split(LINE_SEPARATOR);
            messages[i] = logLines[3];
        }
        return messages;
    }

}