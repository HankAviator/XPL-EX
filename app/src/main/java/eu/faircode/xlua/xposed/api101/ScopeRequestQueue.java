package eu.faircode.xlua.xposed.api101;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/** Accessed only on the scope worker. Keeps assignments made before service delivery. */
final class ScopeRequestQueue {
    private final Set<String> pending = new LinkedHashSet<>();
    private final Set<String> inFlight = new HashSet<>();

    void add(String packageName) {
        if (packageName == null || packageName.isEmpty() || "global".equals(packageName))
            return;
        String scope = "android".equals(packageName) ? "system" : packageName;
        if (!inFlight.contains(scope))
            pending.add(scope);
    }

    Set<String> take(Set<String> currentScope) {
        pending.removeAll(currentScope);
        Set<String> request = new LinkedHashSet<>(pending);
        pending.clear();
        inFlight.addAll(request);
        return request;
    }

    void complete(Set<String> request) {
        inFlight.removeAll(request);
    }

    void disconnected() {
        pending.addAll(inFlight);
        inFlight.clear();
    }
}
