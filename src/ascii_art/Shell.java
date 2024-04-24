package ascii_art;
import java.io.File;
import java.util.TreeSet;
import image.Image;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import java.io.IOException;
import Exceptions.*;

/**
 * This class provides the user interface for running ASCII art generation.
 * It allows the user to interactively control various parameters such as
 * image selection, character sets, resolution, and output method.
 * The main method initializes the shell and starts the program.
 * @author Daniel, Inbar
 */
public class Shell {
    private static final int DEFAULT_RESOLUTION = 128;
    private static final int COMMAND_INDEX = 0;
    private static final int COMMAND_MAX_LENGTH = 2;
    private AsciiArtAlgorithm algorithm;
    private String imgPath;
    private int resolution;
    private String output;
    private TreeSet<Character> charset;
    private Image image;
    private boolean IsChangedImage = true;
    private boolean IsChangedNotes = true;

    /**
     * Constructs a new Shell instance with default parameters.
     * The default image path is set to "src/examples/cat.jpeg",
     * and the default resolution is 128.
     */
    public Shell(){
        this.imgPath =
                "C:\\Users\\inbar\\Documents\\OOP\\oop3\\src\\ascii_art\\cat.jpeg";
        this.resolution = DEFAULT_RESOLUTION;
        this.charset = new TreeSet<>();
        // numbers 0-9
        for (int i = 48; i <= 57; i++) {
            charset.add((char) i);
        }
        this.output = "console";
        try {
            this.image = new Image(imgPath);
        } catch (IOException e) {
            System.out.println("Error loading the image: " + e.getMessage());
        }
    }

    /**
     * Helper method to execute commands received by the shell.
     * @param commandParts The array of command parts.
     * @throws WrongInputException If the input command is invalid.
     */
    private void runHelper(String[] commandParts) throws WrongInputException {
        if (commandParts.length > COMMAND_MAX_LENGTH)
            throw new WrongInputException("Did not execute due to incorrect command.");
        switch (commandParts[COMMAND_INDEX]) {
            case "chars":
                displayCharset();
                break;
            case "add":
                try {
                    addToCharset(commandParts[1]);
                } catch (IncorrectFormatException e){
                    System.out.println(e.getMessage());
                }
                break;
            case "remove":
                try {
                    removeFromCharset(commandParts[1]);
                } catch (IncorrectFormatException e){
                    System.out.println(e.getMessage());
                }
                break;
            case "res":
                try {
                    changeResolution(commandParts[1]);
                } catch (IncorrectFormatException | OutOfBoundariesException e){
                    System.out.println(e.getMessage());
                }
                break;
            case "image":
                try {
                    changeImage(commandParts[1]);
                }catch (IncorrectFormatException e){
                    System.out.println(e.getMessage());
                }
                break;
            case "output":
                try {
                    changeOutput(commandParts[1]);
                }catch (IncorrectFormatException e){
                    System.out.println(e.getMessage());
                }
                break;
            case "asciiArt":
                try{
                    runAsciiArt();
                } catch (EmptyCharsetException e){
                    System.out.println(e.getMessage());
                }
                break;
            default:
                throw new WrongInputException("Did not execute due to incorrect command.");
        }
    }

    /**
     * Starts the ASCII art generation program.
     */
    public void run() {
        System.out.print(">>> ");
        boolean running = true;
        while (running) {
            try {
                String command = KeyboardInput.readLine();
                String[] commandParts = command.split(" ");
                if ((commandParts.length == 1) && (commandParts[0].equals("exit")))
                    running = false;
                else{
                    System.out.print(">>> ");
                    runHelper(commandParts);
                }
            } catch (WrongInputException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Displays the current character set.
     */
    private void displayCharset() {
        System.out.println(charset);
    }

    /**
     * Adds characters to the character set based on user input.
     * @param input The input string specifying characters to add.
     * @throws IncorrectFormatException If the input format is incorrect.
     */
    private void addToCharset(String input) throws IncorrectFormatException {
        // if we get m-p or p-m add all chars between
        if (input.length() == 3 && input.charAt(1) == '-') {
            char start = input.charAt(0);
            char end = input.charAt(2);
            if (start <= end) {
                for (char i = start; i <= end; i++) {
                    charset.add(i);
                    IsChangedNotes = true;
                }
            } else {
                for (char i = end; i <= start; i++) {
                    charset.add(i);
                    IsChangedNotes = true;
                }
            }
        }
        else if (input.equals("space")){
            charset.add(' ');
            IsChangedNotes = true;
        }

        else if (input.equals("all")) {
            for (int i = 32; i <= 126; i++) {
                charset.add((char) i);
            }
            IsChangedNotes = true;
        }
        else if (input.length() == 1){
            charset.add(input.charAt(0));
            IsChangedNotes = true;
        }
        else{
            throw new IncorrectFormatException("Did not add due to incorrect format.");
        }
    }

    /**
     * Removes characters from the character set based on user input.
     * @param input The input string specifying characters to remove.
     * @throws IncorrectFormatException If the input format is incorrect.
     */
    private void removeFromCharset(String input) throws IncorrectFormatException {
        if (input.length() == 3 && input.charAt(1) == '-') {
            char start = input.charAt(0);
            char end = input.charAt(2);
            if (start < end) {
                for (char i = start; i <= end; i++) {
                    charset.remove(i);
                    IsChangedNotes = true;
                }
            } else {
                for (char i = end; i <= start; i++) {
                    charset.remove(i);
                    IsChangedNotes = true;
                }
            }
        }
        else if (input.equals("space")) {
            charset.remove(' ');
            IsChangedNotes = true;
        }
        else if (input.equals("all")) {
            for (int i = 32; i <= 126; i++) {
                charset.remove((char) i);
                IsChangedNotes = true;
            }
        } else if (input.length() == 1 && (int) input.charAt(0) >= 32 &&
                (int) input.charAt(0) <= 126){
            charset.remove(input.charAt(0));
            IsChangedNotes = true;
        }
        else {
            throw new IncorrectFormatException("Did not remove due to incorrect format.");
        }
    }

    /**
     * Changes the resolution of the image.
     * @param input The input string specifying the resolution change.
     * @throws IncorrectFormatException If the input format is incorrect.
     * @throws OutOfBoundariesException If the resolution change exceeds image boundaries.
     */
    private void changeResolution(String input) throws IncorrectFormatException, OutOfBoundariesException {
        // minCharsInRow = max(1, imgWidth/imgHeight)
        int minCharsInRow = Math.max(1, image.getWidth() / image.getHeight());
        int maxResolution = image.getWidth();

        if (input.equals("up")&& resolution*2 <= maxResolution) {
            resolution *= 2;
            if (!IsChangedImage)
                algorithm.SetResolution(resolution);
        } else if (input.equals("down") && resolution/2 >= minCharsInRow) {
            resolution /= 2;
            if (!IsChangedImage)
                algorithm.SetResolution(resolution);
        } else if (input.equals("up") || input.equals("down")) {
            throw new OutOfBoundariesException("Did not change resolution due to exceeding boundaries.");
        }
        else {
            throw new IncorrectFormatException(" Did not change resolution due to incorrect format.");
        }
    }

    /**
     * Changes the image being processed.
     * @param input The input string specifying the new image path.
     * @throws IncorrectFormatException If the input format is incorrect.
     */
    private void changeImage(String input) throws IncorrectFormatException {
        if (input.equals(imgPath))
            return;

        if (new File(input).exists()) {
            imgPath = input;
            try {
                Image img = new Image(input);
                imgPath = input;
                image = img;
                IsChangedImage = true;
            }catch (IOException e) {
                System.out.println("Did not execute due to problem with image file.");
            }
        } else {
            throw new IncorrectFormatException("Did not change output method due to incorrect format.");
        }
    }

    /**
     * Changes the output method.
     * @param input The input string specifying the output method ("console" or "html").
     * @throws IncorrectFormatException If the input format is incorrect.
     */
    private void changeOutput(String input) throws IncorrectFormatException {
        if (input.equals("console") || input.equals("html")) {
            output = input;
        } else {
            throw new IncorrectFormatException("Did not change output method due to incorrect format.");
        }
    }

    /**
     * Runs the ASCII art generation algorithm.
     * @throws EmptyCharsetException If the character set is empty.
     */
    private void runAsciiArt() throws EmptyCharsetException{
        AsciiArtAlgorithm alg;
        if(charset.isEmpty()){
            throw new EmptyCharsetException("Did not execute. Charset is empty.");
        }
        if (!IsChangedImage){
            alg = algorithm;
            if (IsChangedNotes){
                char[] notes = ChangeNotes();
                alg.SetNotes(notes);
            }
        }
        else {
            IsChangedNotes = false;
            IsChangedImage = false;
            char[] notes = ChangeNotes();
            alg = new AsciiArtAlgorithm(image, resolution, notes);
            algorithm = alg;
        }
        char[][] arr = alg.run();
        // Check if the output type is HTML or console
        if (output.equals("html")) {
            HtmlAsciiOutput htmlOutput = new HtmlAsciiOutput("out.html", "Courier New");
            htmlOutput.out(arr); // Output to HTML
        } else {
            // Output to console
            ConsoleAsciiOutput consoleOutput = new ConsoleAsciiOutput();
            consoleOutput.out(arr);
        }
    }

    /**
     * makes a new notes arr
     * @return the notes
     */
    private char[] ChangeNotes(){
        char[] notes = new char[charset.size()];
        int i = 0;
        for (char c : charset) {
            notes[i] = c;
            i++;
        }
        return notes;
    }

    /**
     * Main method to start the program.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        Shell asciiArtShell = new Shell();
        asciiArtShell.run();
    }
}


