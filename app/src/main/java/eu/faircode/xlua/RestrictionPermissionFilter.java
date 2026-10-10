package eu.faircode.xlua;

/** Matches stable hook group IDs to manifest permissions, independent of UI language.
 * Groups without an Android permission remain available to every app.
 */
final class RestrictionPermissionFilter {
    static String[] permissions(String group) {
        if (group == null) return new String[0];
        switch (group) {
            case "Use.Camera": case "Record.Video": return new String[]{"android.permission.CAMERA"};
            case "Record.Audio": return new String[]{"android.permission.RECORD_AUDIO"};
            case "Get.Location": return new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};
            case "Get.Contacts": return new String[]{"android.permission.READ_CONTACTS", "android.permission.WRITE_CONTACTS"};
            case "Get.Calendars": return new String[]{"android.permission.READ_CALENDAR", "android.permission.WRITE_CALENDAR"};
            case "Get.Call.log": return new String[]{"android.permission.READ_CALL_LOG", "android.permission.WRITE_CALL_LOG"};
            case "Get.Messages": return new String[]{"android.permission.READ_SMS", "android.permission.RECEIVE_SMS", "android.permission.RECEIVE_MMS"};
            case "Send.Messages": return new String[]{"android.permission.SEND_SMS"};
            default: return new String[0];
        }
    }

    static boolean matches(String group, String[] requested) {
        String[] required = permissions(group);
        if (required.length == 0) return true;
        if (requested != null)
            for (String permission : required)
                for (String declaration : requested)
                    if (permission.equals(declaration)) return true;
        return false;
    }
}
