package lt.insoft.training.ui.vm.validators;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;

/**
 * Created by povilas.jegelevicius on 2017-03-08.
 */
public class ImageDescriptionValidator extends AbstractValidator{
    @Override
    public void validate(ValidationContext validationContext) {
        if (validationContext.getProperty().getValue() instanceof String){
            String value = (String) validationContext.getProperty().getValue();
            if(value.length()>250){
                validationContext.setInvalid();
                addInvalidMessage(validationContext,"Description is too long");
            }
        } else{
            validationContext.setInvalid();
        }
    }
}
