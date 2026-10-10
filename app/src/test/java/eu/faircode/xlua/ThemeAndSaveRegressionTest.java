package eu.faircode.xlua;

import org.junit.Test;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import static org.junit.Assert.*;

public class ThemeAndSaveRegressionTest {
    @Test public void themeSegmentsKeepGeometryAndAccessibleIconOnlyLabels() throws Exception {
        NodeList buttons = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse("src/main/res/layout/advanced_settings_content.xml")
                .getElementsByTagName("com.google.android.material.button.MaterialButton");
        int count = 0;
        for (int i = 0; i < buttons.getLength(); i++) {
            Element button = (Element) buttons.item(i);
            if (!button.getAttribute("android:id").startsWith("@+id/btn_theme_")) continue;
            count++;
            assertEquals("", button.getAttribute("android:text"));
            assertEquals("52dp", button.getAttribute("android:layout_height"));
            assertEquals("1", button.getAttribute("android:layout_weight"));
            assertEquals("28dp", button.getAttribute("app:iconSize"));
            assertEquals("textStart", button.getAttribute("app:iconGravity"));
            assertFalse(button.getAttribute("android:contentDescription").isEmpty());
        }
        assertEquals(3, count);
    }

    @Test public void bulkSaveUsesSnapshotAndStopsOnlyAfterActivation() throws Exception {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/eu/faircode/xlua/x/ui/fragments/SettingExFragment.java")), java.nio.charset.StandardCharsets.UTF_8);
        String save = source.substring(source.indexOf("case R.id.flSettingsButtonFour:"),
                source.indexOf("case R.id.flSettingsButtonTwo:"));
        assertTrue(save.contains("ActionPacket.create(ActionFlag.PUSH, false)"));
        assertTrue(save.indexOf("getSettingPackets(") < save.indexOf("new Thread"));
        assertTrue(save.indexOf("ForceStopAppCommand.stop") > save.indexOf("SettingHookAutoActivator.activate"));
        assertTrue(save.contains("holder.setValue(savedValue, false)"));
    }
}
