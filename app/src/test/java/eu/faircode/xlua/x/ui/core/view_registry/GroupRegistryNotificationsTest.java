package eu.faircode.xlua.x.ui.core.view_registry;

import org.junit.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;

public class GroupRegistryNotificationsTest {
    @Test public void deliversOnlyToRequestedGroup() {
        GroupRegistryNotifications registry = new GroupRegistryNotifications(null);
        AtomicInteger calls = new AtomicInteger();
        registry.putGroupChangeListener(packet -> {
            assertTrue(packet.isFrom("setting_container_groups"));
            calls.incrementAndGet();
        }, "con::battery");
        registry.putGroupChangeListener(packet -> fail("Wrong recipient"), "con::cpu");
        registry.notifyGroupChange("con::battery", "setting_container_groups");
        assertEquals(1, calls.get());
    }

    @Test public void missingAndInvalidGroupsAreSafe() {
        GroupRegistryNotifications registry = new GroupRegistryNotifications(null);
        registry.putGroupChangeListener(packet -> fail("Invalid event delivered"), "valid");
        registry.notifyGroupChange("offscreen", "changed");
        registry.notifyGroupChange(null, "changed");
        registry.notifyGroupChange("", "changed");
        registry.notifyGroupChange("valid", null);
        registry.notifyGroupChange("valid", "");
        registry.putGroupChangeListener(packet -> fail(), null);
        registry.putGroupChangeListener(packet -> fail(), "");
    }

    @Test public void recycledListenerIsRemovedAndReboundListenerReceivesEvents() {
        GroupRegistryNotifications registry = new GroupRegistryNotifications(null);
        registry.putGroupChangeListener(packet -> fail("Recycled view called"), "row");
        registry.putGroupChangeListener(null, "row");
        registry.notifyGroupChange("row", "changed");
        AtomicInteger calls = new AtomicInteger();
        registry.putGroupChangeListener(packet -> calls.incrementAndGet(), "row");
        registry.notifyGroupChange("row", "changed");
        assertEquals(1, calls.get());
    }

    @Test(expected = IllegalStateException.class)
    public void realListenerFailureIsNotSwallowed() {
        GroupRegistryNotifications registry = new GroupRegistryNotifications(null);
        registry.putGroupChangeListener(packet -> { throw new IllegalStateException("Listener failure"); }, "row");
        registry.notifyGroupChange("row", "changed");
    }
}
