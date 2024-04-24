package ascii_art;
import image.ImageRenderer;
import image_char_matching.SubImgCharMatcher;
import image.Image;
import java.util.ArrayList;


/**
 * This class implements the algorithm for generating ASCII art from an image.
 * It takes an input image, resolution, and character set, and produces ASCII art.
 * @author Daniel, Inbar
 */
public class AsciiArtAlgorithm {
    private Image img;
    private int resolution;
    private char[] notes;
    private char[][] asciiArtImage;
    private int oldResolution = 0;
    private boolean IsChangedNotes = true;

    /**
     * Constructs an AsciiArtAlgorithm object with the specified parameters.
     * @param img The input image.
     * @param resolution The resolution of the ASCII art.
     * @param notes The character set to use for rendering.
     */
    public AsciiArtAlgorithm(Image img, int resolution, char[] notes){
        this.img = img;
        this.resolution = resolution;
        this.notes = notes;
    }

    /**
     * setter for the resolution
     * @param resolution the resolution
     */
    public void SetResolution(int resolution){
        this.resolution = resolution;
    }

    /**
     * setter for the notes
     * @param notes the notes
     */
    public void SetNotes(char[] notes){
        this.notes = notes;
        IsChangedNotes = true;
    }

    /**
     * Runs the ASCII art generation algorithm.
     * @return A 2D char array representing the generated ASCII art.
     */
    public char[][] run(){
        if (oldResolution == resolution && asciiArtImage != null && !IsChangedNotes)
            return asciiArtImage;

        IsChangedNotes = false;
        SubImgCharMatcher matcher = new SubImgCharMatcher(notes);
        ImageRenderer renderer = new ImageRenderer(img);
        renderer.setResolution(resolution);
        oldResolution = resolution;
        ArrayList<ArrayList<Double>> subImages = renderer.getGrayDividedImages();
        char[][] asciiArtImg = new char[renderer.getSubHeight()][renderer.getSubWidth()];
        for (int i = 0; i < renderer.getSubHeight(); i++) {
            for (int j = 0; j < renderer.getSubWidth(); j++) {
                asciiArtImg[i][j] = matcher.getCharByImageBrightness(subImages.get(i).get(j));
            }
        }
        asciiArtImage = asciiArtImg;
        return asciiArtImg;
    }
}