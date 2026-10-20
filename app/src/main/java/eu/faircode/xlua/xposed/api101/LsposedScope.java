package eu.faircode.xlua.xposed.api101;

import android.content.Context;
import android.os.Process;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.faircode.xlua.BuildConfig;
import io.github.libxposed.service.XposedService;
import io.github.libxposed.service.XposedServiceHelper;

/** Module-app side only: the settings bridge executes in system_server. */
public final class LsposedScope {
    private static final String TAG = "XLua.Scope";
    private static final ExecutorService WORKER = Executors.newSingleThreadExecutor();
    private static final ScopeRequestQueue QUEUE = new ScopeRequestQueue();
    private static XposedService service;
    private static boolean initialized;

    private LsposedScope() { }

    public static synchronized void initialize() {
        if (initialized)
            return;
        initialized = true;
        XposedServiceHelper.registerListener(new XposedServiceHelper.OnServiceListener() {
            @Override
            public void onServiceBind(XposedService bound) {
                WORKER.execute(() -> {
                    if (service != null && service != bound)
                        QUEUE.disconnected();
                    service = bound;
                    flush();
                });
            }

            @Override
            public void onServiceDied(XposedService dead) {
                WORKER.execute(() -> {
                    if (service == dead) {
                        service = null;
                        QUEUE.disconnected();
                    }
                });
            }
        });
    }

    public static void request(Context context, int uid, String packageName) {
        // Service requests affect the calling Android user, not an arbitrary profile.
        if (!BuildConfig.APPLICATION_ID.equals(context.getPackageName())
                || uid < 0 || uid / 100000 != Process.myUid() / 100000)
            return;
        initialize();
        WORKER.execute(() -> {
            QUEUE.add(packageName);
            flush();
        });
    }

    private static void flush() {
        if (service == null)
            return;
        Set<String> request = new HashSet<>();
        try {
            request.addAll(QUEUE.take(new HashSet<>(service.getScope())));
            if (request.isEmpty())
                return;
            Set<String> submitted = new HashSet<>(request);
            XposedService submittingService = service;
            service.requestScope(new ArrayList<>(request), new XposedService.OnScopeEventListener() {
                @Override
                public void onScopeRequestApproved(List<String> approved) {
                    WORKER.execute(() -> {
                        if (service == submittingService)
                            QUEUE.complete(submitted);
                        Log.i(TAG, "Scope approved: " + approved);
                    });
                }

                @Override
                public void onScopeRequestFailed(String message) {
                    WORKER.execute(() -> {
                        if (service == submittingService)
                            QUEUE.complete(submitted);
                        Log.w(TAG, "Scope request failed: " + message);
                    });
                }
            });
        } catch (RuntimeException e) {
            // Scope failure must not turn a successful hook assignment into a failure.
            QUEUE.complete(request);
            for (String scope : request)
                QUEUE.add(scope);
            Log.w(TAG, "Could not request LSPosed scope", e);
        }
    }
}
