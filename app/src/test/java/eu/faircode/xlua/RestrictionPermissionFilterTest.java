package eu.faircode.xlua;

import org.junit.Test;
import static org.junit.Assert.*;

public class RestrictionPermissionFilterTest {
    @Test public void filtersUseStableGroupIdsAndComposeAcrossSelections() {
        String[] camera = {"android.permission.CAMERA"};
        assertTrue(RestrictionPermissionFilter.matches(null, camera));
        assertTrue(RestrictionPermissionFilter.matches("Use.Camera", camera));
        assertTrue(RestrictionPermissionFilter.matches("Record.Video", camera));
        assertFalse(RestrictionPermissionFilter.matches("Record.Audio", camera));
        assertFalse(RestrictionPermissionFilter.matches("Get.Location", camera));
        assertTrue(RestrictionPermissionFilter.matches(null, camera));
    }
    @Test public void locationSupportsEitherPermissionAndAudioRequiresMicrophone() {
        assertTrue(RestrictionPermissionFilter.matches("Get.Location", new String[]{"android.permission.ACCESS_COARSE_LOCATION"}));
        assertTrue(RestrictionPermissionFilter.matches("Get.Location", new String[]{"android.permission.ACCESS_FINE_LOCATION"}));
        assertTrue(RestrictionPermissionFilter.matches("Record.Audio", new String[]{"android.permission.RECORD_AUDIO"}));
        assertFalse(RestrictionPermissionFilter.matches("Use.Camera", null));
        assertFalse(RestrictionPermissionFilter.matches("Record.Audio", new String[0]));
        assertTrue(RestrictionPermissionFilter.matches("Get.Clipboard", new String[0]));
    }
}
