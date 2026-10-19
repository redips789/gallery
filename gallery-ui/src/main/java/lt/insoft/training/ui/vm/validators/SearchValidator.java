package lt.insoft.training.ui.vm.validators;

/**
 * Created by povilas.jegelevicius on 2017-03-28.
 */
public class SearchValidator extends MessageValidator {
    @Override
    public String validationMessage(Object value) {
        String query = value == null ? "" : value.toString();
        if (query.trim().isEmpty()) {
            return "Enter search query";
        }
        if (query.length() > 250) {
            return "Search query is too long";
        }
        return null;
    }
}
