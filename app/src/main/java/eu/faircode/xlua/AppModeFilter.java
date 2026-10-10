package eu.faircode.xlua;

/** App mode is independent of search text and permission restrictions. */
final class AppModeFilter {
    static boolean matches(String mode, int uid, boolean enabled, boolean system,
                           int icon, int assignments) {
        switch (mode) {
            case "all": return true;
            case "system": return system;
            case "hook": return assignments > 0;
            default: return uid >= 10000 && enabled && ("icon".equals(mode) ? icon > 0 : !system);
        }
    }
}
