package lt.insoft.training.ui.vm;

import lt.insoft.training.ui.vm.validators.ImageDescriptionValidator;
import lt.insoft.training.ui.vm.validators.ImageTagsValidator;
import lt.insoft.training.ui.vm.validators.NameValidator;
import lt.insoft.training.ui.vm.validators.SearchValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestValidators {

    private static final String TOO_LONG = "x".repeat(251);
    private static final String LONGEST = "x".repeat(250);

    @Test
    public void testNameValidator() {
        NameValidator validator = new NameValidator();
        Assertions.assertNull(validator.validationMessage("beach"));
        Assertions.assertNull(validator.validationMessage(LONGEST));
        Assertions.assertEquals("Enter name", validator.validationMessage(""));
        Assertions.assertEquals("Enter name", validator.validationMessage("   "));
        Assertions.assertEquals("Enter name", validator.validationMessage(null));
        Assertions.assertEquals("Name is too long", validator.validationMessage(TOO_LONG));
    }

    @Test
    public void testSearchValidator() {
        SearchValidator validator = new SearchValidator();
        Assertions.assertNull(validator.validationMessage("beach"));
        Assertions.assertEquals("Enter search query", validator.validationMessage(""));
        Assertions.assertEquals("Enter search query", validator.validationMessage(null));
        Assertions.assertEquals("Search query is too long", validator.validationMessage(TOO_LONG));
    }

    @Test
    public void testDescriptionValidatorAllowsEmpty() {
        ImageDescriptionValidator validator = new ImageDescriptionValidator();
        Assertions.assertNull(validator.validationMessage(null));
        Assertions.assertNull(validator.validationMessage(""));
        Assertions.assertNull(validator.validationMessage(LONGEST));
        Assertions.assertEquals("Description is too long", validator.validationMessage(TOO_LONG));
    }

    @Test
    public void testTagsValidator() {
        ImageTagsValidator validator = new ImageTagsValidator();
        Assertions.assertNull(validator.validationMessage(null));
        Assertions.assertNull(validator.validationMessage(""));
        // tidied up when saved rather than rejected
        Assertions.assertNull(validator.validationMessage("beach, Beach,, sea"));
        Assertions.assertEquals("Tags are too long", validator.validationMessage(TOO_LONG));
    }
}
