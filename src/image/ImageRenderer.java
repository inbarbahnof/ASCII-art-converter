package image;

import java.awt.*;
import java.security.PublicKey;
import java.util.ArrayList;

/**
 * Package: image
 * Class: ImageRenderer
 * Description: This class provides functionality for rendering and processing images. It allows
 *              for setting the resolution of the image, dividing the image into smaller sub-images,
 *              and calculating the greyness of each sub-image.
 * @author Daniel, Inbar
 */
public class ImageRenderer {
    private Image image;
    private final int width;
    private final int height;
    private int resolution;
    private Color[][] paddedImage;
    private ArrayList<ArrayList<Color[][]>> dividedImage;
    private ArrayList<ArrayList<Double>> grayDividedImages;
    private int dividedImageHeight;
    private int dividedImageWidth;
    private int newHeight;
    private int newWidth;

    /**
     * Constructor to initialize the ImageRenderer with the given image.
     *
     * @param image The image to be rendered and processed.
     */
    public ImageRenderer(Image image){
        this.image = image;
        this.height = image.getHeight();
        this.width = image.getWidth();
        this.paddedImage = paddingImage();
    }

    /**
     * Sets the resolution of the image.
     *
     * @param resolution The resolution to set.
     */
    public void setResolution(int resolution) {
        if (resolution != this.resolution){
            this.resolution = resolution;
            dividedImage = divideImage(resolution);
            ArrayList<ArrayList<Double>> grayValues = new ArrayList<>();
            for (int i = 0; i < dividedImageHeight; i++) {
                ArrayList<Double> row = new ArrayList<>();
                for (int j = 0; j < dividedImageWidth; j++) {
                    row.add(calculateGreyness(dividedImage.get(i).get(j)));
                }
                grayValues.add(row);
            }
            grayDividedImages = grayValues;
        }
    }

    /**
     * Retrieves the greyness values of the divided images.
     *
     * @return An ArrayList containing the greyness values of the divided images.
     */
    public ArrayList<ArrayList<Double>> getGrayDividedImages(){
        return grayDividedImages;
    }

    /**
     * Retrieves the height of a sub-image.
     *
     * @return The height of a sub-image.
     */
    public int getSubHeight(){
        return dividedImageHeight;
    }

    /**
     * Retrieves the width of a sub-image.
     *
     * @return The width of a sub-image.
     */
    public int getSubWidth(){
        return dividedImageWidth;
    }

    /**
     * Checks if a number is a power of two.
     *
     * @param num The number to check.
     * @return True if the number is a power of two, false otherwise.
     */
    private boolean checkIfMultOfTwo(int num) {
        return num > 0 && (num & (num - 1)) == 0;
    }

    /**
     * Finds the closest power of two to the given number.
     *
     * @param num The number for which to find the closest power of two.
     * @return The closest power of two to the given number.
     */
    private int findClosestMultOfTwo(int num) {
        int newNum = 2;
        if (checkIfMultOfTwo(num)) {
            return num;
        }
        while (num >=newNum) {
            newNum *= 2;
        }
        return newNum;
    }


    /**
     * Pads the image to the closest power of two dimensions.
     *
     * @return The padded image.
     */
    private Color[][] paddingImage() {
        int paddingWidth = (findClosestMultOfTwo(width) - width) / 2;
        int paddingHeight = (findClosestMultOfTwo(height) - height) / 2;
        int newHeight = height + paddingHeight * 2;
        int newWidth = width + paddingWidth * 2;
        Color[][] paddedImage = new Color[newHeight][newWidth];

        padImage(0, paddingHeight, 0, newWidth, paddedImage); // Top
        padImage(newHeight - paddingHeight, newHeight, 0, newWidth, paddedImage); // Bottom
        padImage(0, newHeight, 0, paddingWidth, paddedImage); // Left
        padImage(0, newHeight, newWidth - paddingWidth, newWidth, paddedImage); // Right


        for (int i = paddingHeight; i < newHeight - paddingHeight; i++) {
            for (int j = paddingWidth; j < newWidth - paddingWidth; j++) {
                paddedImage[i][j] = image.getPixel(i - paddingHeight, j - paddingWidth);
            }
        }
        this.newWidth = newWidth;
        this.newHeight = newHeight;
        return paddedImage;
    }

    /**
     * Fills a portion of the image with white pixels.
     *
     * @param startRow  The starting row index.
     * @param totalRows The total number of rows to fill.
     * @param startCol  The starting column index.
     * @param totalCols The total number of columns to fill.
     * @param image     The image array to fill.
     */
    private void padImage(int startRow, int totalRows, int startCol, int totalCols, Color[][] image) {
        for (int i = startRow; i < totalRows; i++) {
            for (int j = startCol; j < totalCols; j++) {
                image[i][j] = Color.WHITE;
            }
        }
    }

    /**
     * Divides the image into smaller sub-images based on the specified number of subdivisions in a row.
     * Each sub-image will have approximately equal width and height.
     *
     * @param amountOfSubsInRow The number of subdivisions to create in a row.
     * @return An ArrayList containing the sub-images.
     */
    public ArrayList<ArrayList<Color[][]>> divideImage(int amountOfSubsInRow) {
        int sizeOfPicture = newWidth / amountOfSubsInRow;
        if (newWidth < amountOfSubsInRow)
            sizeOfPicture = 1;
        int amountOfSubsInCol = newHeight / sizeOfPicture;
        dividedImageWidth = amountOfSubsInRow;
        dividedImageHeight = amountOfSubsInCol;
        ArrayList<ArrayList<Color[][]>> dev = new ArrayList<>();


        for (int row = 0; row < amountOfSubsInRow; row++) {
            ArrayList<Color[][]> subImages = new ArrayList<>();
            for (int col = 0; col < amountOfSubsInCol; col++) {
                int startCol = col * sizeOfPicture;
                int startRow = row * sizeOfPicture;
                Color[][] subImg = new Color[sizeOfPicture][sizeOfPicture];

                for (int i = 0; i < sizeOfPicture; i++) {
                    for (int j = 0; j < sizeOfPicture; j++) {
                        subImg[i][j] = paddedImage[startRow + i][startCol + j];
                    }
                }
                subImages.add(subImg);
            }
            dev.add(subImages);
        }
        return dev;
    }

    /**
     * Calculates the greyness value of a given image.
     *
     * @param originalImage The image for which to calculate the greyness value.
     * @return The greyness value of the image.
     */
    private double calculateGreyness(Color[][] originalImage) {
        int rows = originalImage.length;
        int cols = originalImage[0].length;
        int sumOfPixels = rows * cols;
        double greySum = 0;

        for (int i = 0; i < originalImage.length; i++) {
            for (int j = 0; j < originalImage[0].length; j++) {
                double greyPixel = originalImage[i][j].getRed() * 0.2126 +
                        originalImage[i][j].getGreen() * 0.7152 + originalImage[i][j].getBlue() * 0.0722;

                greySum += greyPixel;
            }
        }

        greySum = greySum / (255 * sumOfPixels);
        return greySum;
    }
}