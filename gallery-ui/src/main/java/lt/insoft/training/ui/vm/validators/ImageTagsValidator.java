package lt.insoft.training.ui.vm.validators;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;

/**
 * Created by povilas.jegelevicius on 2017-03-08.
 */
public class ImageTagsValidator extends AbstractValidator{
    @Override
    public void validate(ValidationContext validationContext) {
        if (validationContext.getProperty().getValue() instanceof String){
            String value = (String) validationContext.getProperty().getValue();
            if(value.length()>250){
                validationContext.setInvalid();
                addInvalidMessage(validationContext,"Tags are too long");
            } else if(checkForDublicates(value)){
                validationContext.setInvalid();
                addInvalidMessage(validationContext,"Duplicates found");
            } else if(checkForEmptyTags(value)){
                validationContext.setInvalid();
                addInvalidMessage(validationContext,"Empty tag found");
            }
        } else{
            validationContext.setInvalid();
        }
    }

    public boolean checkForDublicates(String value){
        String[] tags = value.split(",");
        int j;
        int k;
        for (j=0;j<tags.length;j++) {
            for (k = j + 1; k < tags.length; k++) {
                if (k != j && tags[k].equals(tags[j])) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkForEmptyTags(String value){
        String[] tags = value.split(",");
        if(value.length() == 0 ){
            return false;
        }
        for (String item: tags) {
            if (item.isEmpty()){
                return true;
            }
        }
        if(tags.length==0){
            return true;
        }
        return false;
    }
}
