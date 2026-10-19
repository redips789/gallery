package lt.insoft.training.ui.vm.validators;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;

import javax.print.attribute.standard.Media;

/**
 * Created by povilas.jegelevicius on 2017-03-08.
 */
public class ImageValidator extends AbstractValidator{
    @Override
    public void validate(ValidationContext validationContext) {
        if(validationContext.getProperty().getValue() instanceof Media){
        } else {
            validationContext.setInvalid();
            addInvalidMessage(validationContext,"Wrong image format");
        }
    }
}
