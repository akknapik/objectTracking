import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RadarImageGenerator {

    public static void main(String[] args) {
        int width = 700;
        int height = 400;
        int numberOfImages = 15;
        int movementPerImage = 15;

        Random random = new Random();

        int echoBrightness = 255;
        int echoRadius = 10;
        int numberOfEchoes = 3;
        double whitePixelChance = 0.3;

        List<int[]> echoCoordinates = new ArrayList<>();

        for (int i = 0; i < numberOfEchoes; i++) {
            echoCoordinates.add(new int[]{random.nextInt(width), random.nextInt(height)});
        }

        int moveDirectionX = random.nextInt(3) - 1; // -1: w lewo, 0: bez ruchu, 1: w prawo
        int moveDirectionY = random.nextInt(3) - 1; // -1: w dół, 0: bez ruchu, 1: w górę

        System.out.println("Ruch punktów echo: " + (moveDirectionX == 0 ? "Brak" : (moveDirectionX > 0 ? "Prawo" : "Lewo"))
                + ", " + (moveDirectionY == 0 ? "Brak" : (moveDirectionY > 0 ? "Góra" : "Dół")));

        for (int imageIndex = 1; imageIndex <= numberOfImages; imageIndex++) {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int noise = random.nextInt(70);
                    image.setRGB(x, y, new Color(noise, noise, noise).getRGB());
                }
            }

            for (int[] coordinate : echoCoordinates) {
                int centerX = coordinate[0];
                int centerY = coordinate[1];

                for (int dy = -echoRadius; dy <= echoRadius; dy++) {
                    for (int dx = -echoRadius; dx <= echoRadius; dx++) {
                        int x = centerX + dx;
                        int y = centerY + dy;

                        double distance = Math.sqrt(dx * dx + dy * dy);

                        if (distance <= echoRadius && x >= 0 && x < width && y >= 0 && y < height) {
                            int brightness = (int) (echoBrightness * (1 - distance / echoRadius));
                            brightness = Math.max(0, Math.min(brightness, 255));

                            if (random.nextDouble() < whitePixelChance || distance == 0) {
                                image.setRGB(x, y, new Color(brightness, brightness, brightness).getRGB());
                                System.out.println(brightness);
                            }
                        }
                    }
                }
            }


            try {
                File output = new File("radar_simulation_" + imageIndex + ".png");
                ImageIO.write(image, "png", output);
                System.out.println("Obraz radarowy " + imageIndex + " wygenerowany: radar_simulation_" + imageIndex + ".png");
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < echoCoordinates.size(); i++) {
                int[] coordinate = echoCoordinates.get(i);
                coordinate[0] += moveDirectionX * movementPerImage;
                coordinate[1] += moveDirectionY * movementPerImage;

                if (coordinate[0] < 0 || coordinate[0] >= width || coordinate[1] < 0 || coordinate[1] >= height) {
                    echoCoordinates.remove(i);
                    i--;
                }
            }
        }
    }
}
