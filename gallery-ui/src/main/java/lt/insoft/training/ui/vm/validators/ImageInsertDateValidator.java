package lt.insoft.training.ui.vm.validators;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;

import java.time.LocalDateTime;

/**
 * Created by povilas.jegelevicius on 2017-03-08.
 */
public class ImageInsertDateValidator extends AbstractValidator {
    @Override
    public void validate(ValidationContext validationContext) {
        LocalDateTime value = (LocalDateTime) (validationContext.getProperty().getValue());
        //String value = (String) validationContext.getProperty().getValue();
        validationContext.setInvalid();
    }
}
