package lt.insoft.training.ui.vm.validators;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;

/**
 * A validator that always tells the user why a value was rejected.
 */
public abstract class MessageValidator extends AbstractValidator {

    @Override
    public void validate(ValidationContext validationContext) {
        String message = validationMessage(validationContext.getProperty().getValue());
        if (message != null) {
            validationContext.setInvalid();
            addInvalidMessage(validationContext, message);
        }
    }

    /**
     * @return why the value is invalid, or null if it is valid
     */
    public abstract String validationMessage(Object value);
}
