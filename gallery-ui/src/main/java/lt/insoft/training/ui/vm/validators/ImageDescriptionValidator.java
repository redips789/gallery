package lt.insoft.training.ui.vm.validators;

/**
 * The description is optional.
 *
 * Created by povilas.jegelevicius on 2017-03-08.
 */
public class ImageDescriptionValidator extends MessageValidator {
    @Override
    public String validationMessage(Object value) {
        if (value != null && value.toString().length() > 250) {
            return "Description is too long";
        }
        return null;
    }
}
