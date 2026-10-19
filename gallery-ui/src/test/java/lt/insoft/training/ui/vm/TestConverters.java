package lt.insoft.training.ui.vm;

import lt.insoft.training.ui.vm.util.DateFormatConverter;
import lt.insoft.training.ui.vm.util.ImageToZkImageConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.zkoss.bind.BindContext;
import org.zkoss.image.AImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;

public class TestConverters {

    /** A BindContext that only answers getConverterArg, which is all the converters use. */
    private static BindContext withFormat(String format) {
        return (BindContext) Proxy.newProxyInstance(BindContext.class.getClassLoader(),
                new Class<?>[]{BindContext.class},
                (proxy, method, args) -> "getConverterArg".equals(method.getName()) && "format".equals(args[0])
                        ? format : null);
    }

    @Test
    public void testDateToUi() {
        DateFormatConverter converter = new DateFormatConverter();
        BindContext ctx = withFormat("yyyy-MM-dd");
        Assertions.assertEquals("2017-04-06", converter.coerceToUi(LocalDateTime.of(2017, 4, 6, 14, 30), null, ctx));
        Assertions.assertNull(converter.coerceToUi(null, null, ctx));
    }

    @Test
    public void testDateToBean() {
        DateFormatConverter converter = new DateFormatConverter();
        BindContext ctx = withFormat("yyyy-MM-dd HH:mm");
        Assertions.assertEquals(LocalDateTime.of(2017, 4, 6, 14, 30), converter.coerceToBean("2017-04-06 14:30", null, ctx));
        Assertions.assertNull(converter.coerceToBean("not a date", null, ctx));
    }

    @Test
    public void testDateWithoutFormat() {
        Assertions.assertThrows(NullPointerException.class,
                () -> new DateFormatConverter().coerceToUi(LocalDateTime.now(), null, withFormat(null)));
    }

    @Test
    public void testImageToUi() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(30, 20, BufferedImage.TYPE_INT_RGB), "png", out);
        AImage image = new ImageToZkImageConverter().coerceToUi(out.toByteArray(), null, null);
        Assertions.assertEquals(30, image.getWidth());
        Assertions.assertEquals(20, image.getHeight());
    }

    @Test
    public void testEmptyImageToUi() {
        ImageToZkImageConverter converter = new ImageToZkImageConverter();
        Assertions.assertNull(converter.coerceToUi(null, null, null));
        Assertions.assertNull(converter.coerceToUi(new byte[0], null, null));
    }
}
