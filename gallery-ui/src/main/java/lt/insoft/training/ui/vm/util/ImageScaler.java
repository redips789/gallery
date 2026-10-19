package lt.insoft.training.ui.vm.util;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.imgscalr.Scalr.*;

/**
 * Created by povilas.jegelevicius on 2017-02-28.
 */
public class ImageScaler {

    public static byte[] scaleImage(byte[] image, int marginLength, int addedPaddingToBottomOfImage) {

        Log logger = LogFactory.getLog(ImageScaler.class);

        if (image == null) {
            logger.error("Error occured, image==null");
            throw new IllegalArgumentException("image is null");
        }
        try (InputStream in = new ByteArrayInputStream(image); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            if (image != null) {
                BufferedImage bImage = ImageIO.read(in);
                if (bImage == null) {
                    logger.warn("Unsupported image format, cannot scale");
                    return null;
                }
                int whiteSpace = 300;
                BufferedImage thumbnail;
                if (bImage.getWidth() >= bImage.getHeight()) {
                    thumbnail = resize(bImage,
                            Method.SPEED,
                            Mode.FIT_TO_WIDTH,
                            marginLength,
                            marginLength,
                            OP_ANTIALIAS);
                    BufferedImage padded =
                            new BufferedImage(marginLength, thumbnail.getHeight() + 2 * whiteSpace
                                    , thumbnail.getType());
                    Graphics g = padded.getGraphics();
                    g.setColor(Color.white);
                    g.fillRect(0, 0, thumbnail.getWidth()
                            , thumbnail.getHeight() + 2 * whiteSpace);
                    g.drawImage(thumbnail, 0, whiteSpace, null);
                    g.dispose();
                    thumbnail = Scalr.crop(padded, 0, whiteSpace - (marginLength - thumbnail.getHeight()) / 2, marginLength
                            , marginLength + addedPaddingToBottomOfImage);
                } else {
                    thumbnail = resize(bImage,
                            Method.SPEED,
                            Mode.FIT_TO_HEIGHT,
                            marginLength,
                            marginLength,
                            OP_ANTIALIAS);
                    BufferedImage padded =
                            new BufferedImage(thumbnail.getWidth() + 2 * whiteSpace, marginLength
                                    , thumbnail.getType());
                    Graphics g = padded.getGraphics();
                    g.setColor(Color.white);
                    g.fillRect(0, 0, thumbnail.getWidth() + 2 * whiteSpace, thumbnail.getHeight()+addedPaddingToBottomOfImage);
                    g.drawImage(thumbnail, whiteSpace, 0, null);
                    g.dispose();
                    thumbnail = Scalr.crop(padded, whiteSpace - (marginLength - thumbnail.getWidth()) / 2, 0, marginLength
                            , marginLength + addedPaddingToBottomOfImage);
//                    whiteSpace = addedPaddingToBottomOfImage;
//                    padded = new BufferedImage(marginLength
//                            , thumbnail.getHeight() + whiteSpace, thumbnail.getType());
//                    g = padded.getGraphics();
//                    g.setColor(Color.white);
//                    g.fillRect(0, 0, thumbnail.getWidth(), thumbnail.getHeight() + whiteSpace);
//                    g.drawImage(thumbnail, 0, 0, null);
//                    g.dispose();
//                    thumbnail = padded;
                }
                ImageIO.write(thumbnail, "png", baos);
                baos.flush();
                byte[] imageInByte = baos.toByteArray();
                return imageInByte;
            } else {
                logger.error("Error occured, image==null");
                throw new IllegalArgumentException();
            }
        } catch (IOException e) {
            logger.error("Error occured while scalling image", e);
            return null;
        }
    }
}
