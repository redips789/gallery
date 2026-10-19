package lt.insoft.training.ui.vm.validators;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.validator.AbstractValidator;

/**
 * Created by povilas.jegelevicius on 2017-03-07.
 */
public class NameValidator extends AbstractValidator {
    @Override
    public void validate(ValidationContext validationContext) {
        if (validationContext.getProperty().getValue() instanceof String){
            String value = (String) validationContext.getProperty().getValue();
            if(value.length()>250){
                validationContext.setInvalid();
                addInvalidMessage(validationContext,"Name is too long");
            } else if(value.length() == 0){
                validationContext.setInvalid();
                addInvalidMessage(validationContext,"Enter name");
            }
        } else{
                       validationContext.setInvalid();
        }
    }
}
