package lt.insoft.training.ui.vm.validators;

import lt.insoft.training.ui.vm.util.Tags;

/**
 * Tags are optional. Spaces, empty tags and duplicates are tidied up by {@link Tags#parse}, so only
 * the length is checked here; each tag is stored in a 250-character column.
 *
 * Created by povilas.jegelevicius on 2017-03-08.
 */
public class ImageTagsValidator extends MessageValidator {
    @Override
    public String validationMessage(Object value) {
        if (value == null) {
            return null;
        }
        String input = value.toString();
        if (input.length() > 250) {
            return "Tags are too long";
        }
        return null;
    }
}
