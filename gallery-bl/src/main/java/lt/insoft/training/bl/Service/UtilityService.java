package lt.insoft.training.bl.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Date;

/**
 * Created by povilas.jegelevicius on 2017-03-30.
 */
public class UtilityService {

    // accepts 2017-04-06 as well as 2017-4-6; the whole input must be a valid date
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-M-d").withResolverStyle(ResolverStyle.STRICT);

    public String escapeSpecialCharacters(String searchInput) {
        // LIKE wildcards plus the escape character itself ('!'), which must go first
        String[] specialCharacters = {"!", "%", "_"};
        for (String item : specialCharacters) {
            searchInput = searchInput.replace(item, "!" + item);
        }
        return searchInput;
    }

    /**
     * @return the date at the start of that day, or null if the input is not exactly a yyyy-MM-dd date
     */
    public Date parseDateService(String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date.trim(), DATE_FORMAT);
            return Date.from(parsedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
