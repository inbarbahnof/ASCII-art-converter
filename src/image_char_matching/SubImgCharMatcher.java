package image_char_matching;

import image_char_matching.CharConverter;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This class matches an ASCII character to a sub-image with a given brightness.
 * It uses brightness values to find the closest character from a provided character set.
 * This class is intended for use in an ASCII art algorithm to replace sub-images with characters.
 * @author inbar, daniel
 */
public class SubImgCharMatcher {
    private final int PIXEL_NUMBER = 16 * 16;
    private TreeMap<Double, TreeSet<Character>> charset;
    private TreeMap<Double, TreeSet<Character>> normalizedCharset;
    private double maxBrightness;
    private double minBrightness;

    /**
     * Constructs a SubImgCharMatcher object with the given character set.
     *
     * @param charset An array of characters to use as the character set.
     */
    public SubImgCharMatcher(char[] charset) {
        this.charset = new TreeMap<>();
        double currBrightness;
        minBrightness = 2;
        maxBrightness = -1;
        for (char c : charset) {
            currBrightness = calculateBrightness(c);
            if (!this.charset.containsKey(currBrightness))
                this.charset.put(currBrightness, new TreeSet<>());
            this.charset.get(currBrightness).add(c);

            minBrightness = Math.min(minBrightness, currBrightness);
            maxBrightness = Math.max(maxBrightness, currBrightness);
        }
        normalizeArray();
    }

    /**
     * Finds the character with the brightness closest to the given brightness value.
     *
     * @param brightness The brightness value to match.
     * @return The character with the closest brightness value in the character set.
     */
    public char getCharByImageBrightness(double brightness) {
        double lessOrEqual = normalizedCharset.floorKey(brightness);
        double moreOrEqual = normalizedCharset.ceilingKey(brightness);
        double distance = Math.abs(lessOrEqual - brightness);

        TreeSet<Character> set;

        if (distance < Math.abs(moreOrEqual - brightness)) {
            set = normalizedCharset.get(lessOrEqual);
        } else {
            set = normalizedCharset.get(moreOrEqual);
        }

        return set.first(); // TreeSet maintains natural ordering, first element will be the minimum
    }

    /**
     * Adds a character to the character set.
     *
     * @param c The character to add.
     */
    public void addChar(char c) {
        double brightness = calculateBrightness(c);
        // check if there is a set of this brightness already
        if (!charset.containsKey(brightness))
            charset.put(brightness, new TreeSet<>());
        charset.get(brightness).add(c);

        // checks if the min/max values has been changed
        if (brightness > maxBrightness || brightness < minBrightness) {
            updateMinMaxBrightness();
            normalizeArray();
        } else {
            double newBrightness = (brightness - minBrightness) / (maxBrightness - minBrightness);
            if (!normalizedCharset.containsKey(newBrightness))
                normalizedCharset.put(newBrightness, new TreeSet<>());
            normalizedCharset.get(newBrightness).add(c);
        }
    }

    /**
     * Removes a character from the character set.
     *
     * @param c The character to remove.
     */
    public void removeChar(char c) {
        double brightness = calculateBrightness(c);
        double newBrightness = (brightness - minBrightness) / (maxBrightness - minBrightness);
        TreeSet<Character> set = charset.get(brightness);
        TreeSet<Character> normalizedSet = normalizedCharset.get(newBrightness);

        // remove from normalized set
        if (normalizedSet != null) {
            normalizedSet.remove(c);
            if (normalizedSet.isEmpty())
                normalizedCharset.remove(newBrightness);
        }

        // remove from regular set, update min/max
        if (set != null) {
            set.remove(c);
            if (set.isEmpty()) {
                charset.remove(brightness);
                if (brightness == maxBrightness || brightness == minBrightness) {
                    updateMinMaxBrightness();
                    normalizeArray();
                }
            }
        }
    }

    /**
     * Normalizes the brightness values in the character set to a range of 0 to 1.
     */
    private void normalizeArray() {
        TreeMap<Double, TreeSet<Character>> newCharset = new TreeMap<>();
        for (Map.Entry<Double, TreeSet<Character>> entry : charset.entrySet()) {
            Double oldBrightness = entry.getKey();
            TreeSet<Character> characters = entry.getValue();

            Double newBrightness = (oldBrightness - minBrightness) / (maxBrightness - minBrightness);
            newCharset.put(newBrightness, characters);
        }
        normalizedCharset = newCharset;
    }

    /**
     * Updates the minimum and maximum brightness values in the character set.
     */
    private void updateMinMaxBrightness() {
        if (charset.isEmpty()) {
            minBrightness = -1;
            maxBrightness = 2;
        } else {
            minBrightness = charset.firstKey();
            maxBrightness = charset.lastKey();
        }
    }

    /**
     * Calculates the brightness of a given character.
     *
     * @param c The character to calculate the brightness for.
     * @return The brightness value of the character.
     */
    private double calculateBrightness(char c) {
        boolean[][] boolArray = CharConverter.convertToBoolArray(c);
        double countTrue = 0;

        for (boolean[] row : boolArray) {
            for (boolean pixel : row) {
                if (pixel) {
                    countTrue++;
                }
            }
        }
        return countTrue / PIXEL_NUMBER;
    }
}