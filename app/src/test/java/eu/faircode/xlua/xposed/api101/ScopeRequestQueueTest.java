package eu.faircode.xlua.xposed.api101;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;

public class ScopeRequestQueueTest {
    @Test
    public void queuesUntilServiceArrivesAndSkipsAlreadyScopedPackages() {
        ScopeRequestQueue queue = new ScopeRequestQueue();
        queue.add("com.example.scoped");
        queue.add("com.example.target");
        queue.add("com.example.target");
        assertEquals(Collections.singleton("com.example.target"),
                queue.take(Collections.singleton("com.example.scoped")));
    }

    @Test
    public void doesNotRepeatOutstandingRequestButAllowsRetryAfterDenial() {
        ScopeRequestQueue queue = new ScopeRequestQueue();
        queue.add("com.example.target");
        Set<String> request = queue.take(Collections.emptySet());
        queue.add("com.example.target");
        assertTrue(queue.take(Collections.emptySet()).isEmpty());
        queue.complete(request);
        queue.add("com.example.target");
        assertEquals(request, queue.take(Collections.emptySet()));
    }

    @Test
    public void reconnectChecksActualScopeBeforeResubmitting() {
        ScopeRequestQueue queue = new ScopeRequestQueue();
        queue.add("com.example.target");
        queue.take(Collections.emptySet());
        queue.disconnected();
        assertTrue(queue.take(Collections.singleton("com.example.target")).isEmpty());
    }

    @Test
    public void mapsFrameworkScopeAndIgnoresGlobalSettings() {
        ScopeRequestQueue queue = new ScopeRequestQueue();
        queue.add(null);
        queue.add("");
        queue.add("global");
        queue.add("android");
        queue.add("system");
        assertEquals(Collections.singleton("system"), queue.take(Collections.emptySet()));
    }
}
