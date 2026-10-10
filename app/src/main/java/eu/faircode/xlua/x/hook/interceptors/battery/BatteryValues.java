package eu.faircode.xlua.x.hook.interceptors.battery;

final class BatteryValues {
    static Object result(long value, boolean isLong) {
        // A numeric ternary promotes int to long before boxing, breaking getIntProperty.
        if (isLong) return Long.valueOf(value);
        return Integer.valueOf((int)Math.min(Integer.MAX_VALUE, value));
    }

    static long chargeCounter(Integer capacityMah, Integer percentage) {
        if (capacityMah == null || percentage == null) return -1;
        return (long)Math.max(3000, Math.min(10000, capacityMah)) * 1000L
                * Math.max(0, Math.min(100, percentage)) / 100L;
    }
}
