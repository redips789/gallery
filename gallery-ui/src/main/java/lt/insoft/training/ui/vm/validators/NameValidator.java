package lt.insoft.training.ui.vm.validators;

/**
 * Created by povilas.jegelevicius on 2017-03-07.
 */
public class NameValidator extends MessageValidator {
    @Override
    public String validationMessage(Object value) {
        String name = value == null ? "" : value.toString();
        if (name.trim().isEmpty()) {
            return "Enter name";
        }
        if (name.length() > 250) {
            return "Name is too long";
        }
        return null;
    }
}
