package lt.insoft.training.ui.vm;

import lt.insoft.training.ui.vm.util.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestTags {

    @Test
    public void testParseTrimsTags() {
        Assertions.assertEquals(List.of("beach", "sea"), Tags.parse("beach, sea"));
        Assertions.assertEquals(List.of("beach", "sea"), Tags.parse("  beach ,sea  "));
    }

    @Test
    public void testParseDropsEmptyTags() {
        Assertions.assertEquals(List.of("beach", "sea"), Tags.parse("beach,, ,sea,"));
        Assertions.assertEquals(List.of(), Tags.parse(""));
        Assertions.assertEquals(List.of(), Tags.parse(" , "));
        Assertions.assertEquals(List.of(), Tags.parse(null));
    }

    @Test
    public void testParseDropsDuplicatesIgnoringCase() {
        Assertions.assertEquals(List.of("Sea", "beach"), Tags.parse("Sea, beach, sea, SEA"));
    }

    @Test
    public void testParseReturnsModifiableList() {
        List<String> tags = Tags.parse("beach");
        tags.add("sea");
        Assertions.assertEquals(2, tags.size());
    }

    @Test
    public void testJoin() {
        Assertions.assertEquals("beach, sea", Tags.join(List.of("beach", "sea")));
        Assertions.assertEquals("", Tags.join(List.of()));
        Assertions.assertEquals("", Tags.join(null));
    }

    @Test
    public void testJoinThenParseRoundTrips() {
        List<String> tags = List.of("beach", "sea");
        Assertions.assertEquals(tags, Tags.parse(Tags.join(tags)));
    }
}
