package eu.faircode.xlua;

import org.junit.Test;
import java.util.HashSet;
import static org.junit.Assert.*;

public class XUiGroupTest {
    @Test public void allGroupSupportsEqualityAndHashCollections() {
        XUiGroup first = new XUiGroup();
        XUiGroup second = new XUiGroup();
        assertEquals(first, second);
        HashSet<XUiGroup> groups = new HashSet<>();
        groups.add(first);
        assertTrue(groups.contains(second));
        assertNotEquals(first, null);
        second.name = "Use.Camera";
        assertNotEquals(first, second);
    }

    @Test public void identityDoesNotDependOnTranslatedTitle() {
        XUiGroup first = new XUiGroup();
        XUiGroup second = new XUiGroup();
        first.name = second.name = "Use.Camera";
        first.title = "Camera";
        second.title = "Камера";
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
