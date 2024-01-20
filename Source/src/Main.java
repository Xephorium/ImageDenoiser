import io.FileManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Image Denoiser - Reduces the noise of an image sequence. :)

public class Main {

    public static void main(String[] args) throws IOException {

        int MINIMUM_IMAGES = 3;
        int IMAGES_TO_ANALYZE = 20;
        int IMAGE_HEIGHT = 500;

        List<File> images = FileManager.getDirectoryFiles(new File("input"));

        if (images.size() >= MINIMUM_IMAGES) {

            // Read First Image Data
            BufferedImage bufferedImage = ImageIO.read(images.get(0));
            int imageWidth = bufferedImage.getWidth();
            int imageHeight = IMAGE_HEIGHT; // bufferedImage.getHeight();

            // Initialize Color List
            ArrayList<ArrayList<ArrayList<Integer>>> pixelColors = new ArrayList<>();

            // Read All Pixel Values
            for (int i = 0; i < IMAGES_TO_ANALYZE; i++) {
                System.out.println("Reading Image #" + (i + 1));
                BufferedImage buffImage = ImageIO.read(images.get(i));
                for (int x = 0; x < imageWidth; x++) {
                    pixelColors.add(new ArrayList<>());
                    for (int y = 0; y < imageHeight; y++) {
                        pixelColors.get(x).add(new ArrayList<>());
                        Color color = new Color(buffImage.getRGB(x, 750 + y));

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
            }

            // Create Denoised Image
            BufferedImage bi = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_BGR);
            for(int x = 0; x < imageWidth; x++) {
                System.out.println("Calculating column #" + x + " colors.");
                for(int y = 0; y < imageHeight; y++) {
                    bi.setRGB(
                            x,
                            y,
                            new Color(
                                    pixelColors.get(x).get(y).get(0) / IMAGES_TO_ANALYZE,
                                    pixelColors.get(x).get(y).get(1) / IMAGES_TO_ANALYZE,
                                    pixelColors.get(x).get(y).get(2) / IMAGES_TO_ANALYZE
                            ).getRGB()
                    );
                }
            }

            // Write to Output File
            try {
                System.out.println("Writing to Output File");
                ImageIO.write(bi, "PNG", new File("output/output.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}