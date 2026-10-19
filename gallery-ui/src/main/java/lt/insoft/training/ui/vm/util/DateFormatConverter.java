package lt.insoft.training.ui.vm.util;

/**
 * Created by povilas.jegelevicius on 2017-02-22.
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

public class DateFormatConverter implements Converter {

    private Log logger = LogFactory.getLog(DateFormatConverter.class);

    @Override
    public Object coerceToUi(Object val, Component comp, BindContext ctx) {
        //user sets format in annotation of binding or args when calling binder.addPropertyBinding()
        final String format = (String) ctx.getConverterArg("format");
        if (format == null) throw new NullPointerException("format attribute not found");
        final LocalDateTime date = (LocalDateTime) val;
        return date == null ? null : DateTimeFormatter.ofPattern(format).format(date);
    }

    @Override
    public Object coerceToBean(Object val, Component comp, BindContext ctx) {
        final String format = (String) ctx.getConverterArg("format");
        if (format == null) {
            logger.error("Error occured, null pointer exception on coerceToBean");
            throw new NullPointerException("format attribute not found");
        }
        final String date = (String) val;
        try {
            return date == null ? null : LocalDateTime.parse(date, DateTimeFormatter.ofPattern(format));
        } catch (DateTimeParseException e) {
            logger.error("Error occured, unable to parse date");
            return null;
        }
    }
}
