import java.io.Serializable;
import java.io.File;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.IOException;

/**
 * Demo for Berkeley's CS61BL Project2:
 * Example of a serializable object. On the first run
 * of the program, creates the object  (instead of
 * reading the object from the NumberHolder.ser file
 * since there is no NumberHolder.ser file yet). On
 * every other run of the program, prints out
 * my current number (given from the last call to the program)
 * and then saves the users input as my new number.
 *
 * @author Armani Ferrante
 */

public class NumberHolder implements Serializable {

    ///////////////////////////////////////////////////////////////
    /******************* NumberHolder Object *********************/
    ///////////////////////////////////////////////////////////////

    /**
     * Holds my number.
     */
    private int _myNumber;

    /**
     * Constructor defaults number to 0.
     */
    public NumberHolder() {
        System.out.println("New number created!\n");
        _myNumber = 0;
    }

    /**
     * Prints my saved number.
     */
    private void printNumber() {
        System.out.println("My old number is " + _myNumber +"\n");
    }

    /**
     * Sets my _myNumber variable to NUM.
     */
    private void setNumber(int num) {
        System.out.println("Set my number to " + num +"\n");
        _myNumber = num;
    }


    ///////////////////////////////////////////////////////////////
    /************************ Static *****************************/
    ///////////////////////////////////////////////////////////////


    public static void main (String[] args) {
        NumberHolder theNumber = number();

        theNumber.printNumber();
        theNumber.setNumber(convertStringToNumber(args[0]));

        serialWrite(theNumber);
    }

    /**
     * Create a new object if and only if this is the
     * first time running the program. Otherwise, just
     * read from the .ser file (deserealize).
     */
    public static NumberHolder number() {
        if (!aNumberExists()) {
            return new NumberHolder();
        } else {
            return serialRead();
        }
    }

    /**
     * Returns true iff a number holder serializable files
     * exists.
     */
    private static boolean aNumberExists() {
        File savedNumber = new File("NumberHolder.ser");
        return savedNumber.exists();
    }

    /**
     * Utility method: returns the conversion of STR to its number.
     */
    private static int convertStringToNumber(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return -1;
        }
    }


    ///////////////////////////////////////////////////////////////
    /************************ Serial Code ************************/
    ///////////////////////////////////////////////////////////////


    /**
     * Deserializes a NumberHolder from the current directory,
     * returning the saved object.
     */
    public static NumberHolder serialRead()  {
        System.out.println("Reading object from NumberHolder.ser file\n");

        NumberHolder num = null;
        try {
            ObjectInput input = new ObjectInputStream (
                                new FileInputStream(
                                "NumberHolder.ser"));

            num = (NumberHolder) input.readObject();
        } catch (IOException e) {
            System.err.printf("Error: %s\n", e.toString());
        } catch (ClassNotFoundException e2) {
            System.err.printf("Error: %s\n", e2.toString());
        }

        return num;
    }

    /**
     * Serializes HOLDER, an instance of a NumberHolder,
     * saving the file to the current directory.
     */
    public static void serialWrite(NumberHolder holder) {
        System.out.println("Writing number object with number "
                           + holder._myNumber
                           + " to NumberHolder.ser file");

        try {
            ObjectOutput output = new ObjectOutputStream(
                                  new FileOutputStream(
                                  "NumberHolder.ser"));

            output.writeObject(holder);
        } catch (IOException e) {
            System.err.printf("Error: %s\n", e.toString());
        }
    }
}
