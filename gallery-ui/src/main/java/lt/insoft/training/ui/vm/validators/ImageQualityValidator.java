package lt.insoft.training.ui.vm.validators;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;

/**
 * Created by povilas.jegelevicius on 2017-03-08.
 */
public class ImageQualityValidator extends AbstractValidator{
    public void validate(ValidationContext validationContext) {
        if (validationContext.getProperty().getValue() instanceof String){
            validationContext.setInvalid();
        } else{
            validationContext.setInvalid();
        }
    }
}
