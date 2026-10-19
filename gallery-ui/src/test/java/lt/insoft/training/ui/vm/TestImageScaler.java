package lt.insoft.training.ui.vm;

import lt.insoft.training.ui.vm.util.ImageScaler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestImageScaler {

    // the sizes the upload page uses for thumbnails
    private static final int THUMBNAIL = 370;
    private static final int THUMBNAIL_PADDING = 60;

    private static byte[] picture(int width, int height, String format) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, format, out);
        return out.toByteArray();
    }

    private static BufferedImage read(byte[] bytes) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(bytes));
    }

    @Test
    public void testLandscapeThumbnail() throws IOException {
        BufferedImage thumbnail = read(ImageScaler.scaleImage(picture(800, 400, "jpg"), THUMBNAIL, THUMBNAIL_PADDING));
        Assertions.assertEquals(THUMBNAIL, thumbnail.getWidth());
        Assertions.assertEquals(THUMBNAIL + THUMBNAIL_PADDING, thumbnail.getHeight());
    }

    @Test
    public void testPortraitThumbnail() throws IOException {
        // portrait pictures used to fail with "Invalid crop bounds"
        BufferedImage thumbnail = read(ImageScaler.scaleImage(picture(500, 621, "jpg"), THUMBNAIL, THUMBNAIL_PADDING));
        Assertions.assertEquals(THUMBNAIL, thumbnail.getWidth());
        Assertions.assertEquals(THUMBNAIL + THUMBNAIL_PADDING, thumbnail.getHeight());
    }

    @Test
    public void testSquareWithoutPadding() throws IOException {
        BufferedImage scaled = read(ImageScaler.scaleImage(picture(1000, 1000, "png"), 800, 0));
        Assertions.assertEquals(800, scaled.getWidth());
        Assertions.assertEquals(800, scaled.getHeight());
    }

    @Test
    public void testUnreadableImage() {
        Assertions.assertNull(ImageScaler.scaleImage("not a picture".getBytes(), THUMBNAIL, THUMBNAIL_PADDING));
    }

    @Test
    public void testNullImage() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> ImageScaler.scaleImage(null, THUMBNAIL, THUMBNAIL_PADDING));
    }
}
