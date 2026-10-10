package eu.faircode.xlua;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppModeFilterTest {
    @Test public void firstApplicationUidIsIncluded() {
        assertTrue(AppModeFilter.matches("user", 10000, true, false, 1, 0));
        assertFalse(AppModeFilter.matches("user", 9999, true, false, 1, 0));
    }
    @Test public void modesRemainIndependent() {
        assertFalse(AppModeFilter.matches("user", 12000, true, true, 1, 0));
        assertTrue(AppModeFilter.matches("system", 1000, false, true, 0, 0));
        assertFalse(AppModeFilter.matches("hook", 12000, true, false, 1, 0));
        assertTrue(AppModeFilter.matches("hook", 12000, true, false, 1, 1));
        assertTrue(AppModeFilter.matches("all", 1000, false, true, 0, 0));
        assertFalse(AppModeFilter.matches("icon", 12000, true, false, 0, 0));
        assertFalse(AppModeFilter.matches("user", 12000, false, false, 1, 0));
    }
}
