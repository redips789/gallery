package lt.insoft.training.ui.vm.validators;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;

import java.util.Date;

/**
 * Created by povilas.jegelevicius on 2017-03-08.
 */
public class ImageInsertDateValidator extends AbstractValidator {
    @Override
    public void validate(ValidationContext validationContext) {
        Date value = (Date) (validationContext.getProperty().getValue());
        //String value = (String) validationContext.getProperty().getValue();
        validationContext.setInvalid();
    }
}
