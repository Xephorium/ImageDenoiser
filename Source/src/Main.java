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
            int imageHeight = IMAGE_HEIGHT; //bufferedImage.getHeight();

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
                        pixelColors.get(x).get(y).add(buffImage.getRGB(x, 750 + y));
                    }
                }
            }

            // Get Average Pixel Value
            ArrayList<ArrayList<Color>> averagePixelColors = new ArrayList<>();
            for (int x = 0; x < imageWidth; x++) {
                averagePixelColors.add(new ArrayList<>());
                for (int y = 0; y < imageHeight; y++) {
                    int r = 0;
                    int g = 0;
                    int b = 0;
                    for (int i = 0; i < IMAGES_TO_ANALYZE; i++) {
                        Color color = new Color(pixelColors.get(x).get(y).get(i));
                        r += color.getRed();
                        g += color.getGreen();
                        b += color.getBlue();
                    }
                    averagePixelColors.get(x).add(new Color(
                            r / IMAGES_TO_ANALYZE,
                            g / IMAGES_TO_ANALYZE,
                            b / IMAGES_TO_ANALYZE
                    ));
                }
            }

            // Create Denoised Image
            BufferedImage bi = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_BGR);
            for(int x = 0; x < imageWidth; x++) {
                for(int y = 0; y < imageHeight; y++) {
                    bi.setRGB(x, y, averagePixelColors.get(x).get(y).getRGB());
                }
            }

            // Write to Output File
            try {
                // javax.imageio.ImageIO:
                ImageIO.write(bi, "PNG", new File("output/output.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}