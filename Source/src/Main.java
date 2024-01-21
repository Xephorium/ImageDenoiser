import io.FileManager;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Image Denoiser - Reduces the noise of an image sequence.

public class Main {

    public static void main(String[] args) throws IOException, ImageReadException {

        int MINIMUM_IMAGES = 3;

        /* Note: Loading & processing 4K Images uses LOADS of memory. I've increased the JVM
         *       heap size to 12 gigs, but even then the system will run out of space or start
         *       to freeze before processing 75+ 4K images in full. As a workaround, this program
         *       processes all provided images in vertical sections. SLICE_HEIGHT represents the
         *       height of this run's subsection of the image and SLICE_OFFSET represents the
         *       y-value of that slice's start. All sections are processed at full width and can
         *       be stitched back together manually in an external image editing program.
         */
        int SLICE_HEIGHT = 1920;
        int SLICE_OFFSET = 0;

        List<File> images = FileManager.getDirectoryFiles(new File("input"));

        if (images.size() >= MINIMUM_IMAGES) {

            // Read First Image Data
            BufferedImage bufferedImage = Imaging.getBufferedImage(images.get(0));
            int imageWidth = bufferedImage.getWidth();
            int imageHeight = SLICE_HEIGHT; // bufferedImage.getHeight();
            bufferedImage.flush();
            bufferedImage = null;

            // Initialize Color List
            ArrayList<ArrayList<ArrayList<Integer>>> pixelColors = new ArrayList<>();

            // Read All Pixel Values
            for (int i = 0; i < images.size(); i++) {
                System.out.println("Reading Image #" + (i + 1));
                bufferedImage = Imaging.getBufferedImage(images.get(i));
                for (int x = 0; x < imageWidth; x++) {
                    pixelColors.add(new ArrayList<>());
                    for (int y = 0; y < imageHeight; y++) {
                        pixelColors.get(x).add(new ArrayList<>());
                        Color color = new Color(bufferedImage.getRGB(x, SLICE_OFFSET + y));

                        // Add Red Component
                        if (pixelColors.get(x).get(y).size() < 1) pixelColors.get(x).get(y).add(color.getRed());
                        pixelColors.get(x).get(y).set(0, pixelColors.get(x).get(y).get(0) + color.getRed());

                        // Add Green Component
                        if (pixelColors.get(x).get(y).size() < 2) pixelColors.get(x).get(y).add(color.getGreen());
                        pixelColors.get(x).get(y).set(1, pixelColors.get(x).get(y).get(1) + color.getGreen());

                        // Add Blue Component
                        if (pixelColors.get(x).get(y).size() < 3) pixelColors.get(x).get(y).add(color.getBlue());
                        pixelColors.get(x).get(y).set(2, pixelColors.get(x).get(y).get(2) + color.getBlue());
                    }
                }
                bufferedImage.flush();
                bufferedImage = null;
            }

            // Create Denoised Image
            bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_BGR);
            for(int x = 0; x < imageWidth; x++) {
                for(int y = 0; y < imageHeight; y++) {
                    bufferedImage.setRGB(
                            x,
                            y,
                            new Color(
                                    Math.min(Math.max(pixelColors.get(x).get(y).get(0) / images.size(), 0), 255),
                                    Math.min(Math.max(pixelColors.get(x).get(y).get(1) / images.size(), 0), 255),
                                    Math.min(Math.max(pixelColors.get(x).get(y).get(2) / images.size(), 0), 255)
                            ).getRGB()
                    );
                }
            }

            // Write to Output File
            try {
                System.out.println("Writing to Output File");
                ImageIO.write(bufferedImage, "PNG", new File("output/output.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}