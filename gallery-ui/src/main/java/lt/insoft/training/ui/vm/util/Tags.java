package lt.insoft.training.ui.vm.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Converts between the comma-separated tags typed on the upload and edit pages and the stored tag list.
 */
public final class Tags {

    private Tags() {
    }

    /**
     * Splits on commas and trims each tag. Empty tags are dropped, and so are duplicates, ignoring case
     * (search ignores case too); the first spelling is kept.
     */
    public static List<String> parse(String input) {
        List<String> tags = new ArrayList<>();
        if (input == null) {
            return tags;
        }
        Set<String> seen = new HashSet<>();
        for (String part : input.split(",")) {
            String tag = part.trim();
            if (!tag.isEmpty() && seen.add(tag.toLowerCase(Locale.ROOT))) {
                tags.add(tag);
            }
        }
        return tags;
    }

    public static String join(List<String> tags) {
        return tags == null ? "" : String.join(", ", tags);
    }
}
