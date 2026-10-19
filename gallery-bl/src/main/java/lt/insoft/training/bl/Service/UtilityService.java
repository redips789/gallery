package lt.insoft.training.bl.Service;

import lt.insoft.training.model.ImageDetails_;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by povilas.jegelevicius on 2017-03-30.
 */
//@Component("imageService")
public class UtilityService {

    public String escapeSpecialCharacters(String searchInput) {
        // LIKE wildcards plus the escape character itself ('!'), which must go first
        String[] specialCharacters = {"!", "%", "_"};
        for (String item : specialCharacters) {
            searchInput = searchInput.replace(item, "!" + item);
        }
        return searchInput;
    }

    public Date parseDateService(String date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate;
        try {
            df.setLenient(false);
            parsedDate = df.parse(date);
            return parsedDate;
        } catch (ParseException e) {
            return null;
        }
    }
}
