package eu.faircode.xlua.x.hook.interceptors.battery;

import org.junit.Test;
import static org.junit.Assert.*;

public class BatteryValuesTest {
    @Test public void absentSettingsDoNotGenerateChargeCounter() {
        assertEquals(-1L, BatteryValues.chargeCounter(null, 50));
        assertEquals(-1L, BatteryValues.chargeCounter(5000, null));
        assertEquals(-1L, BatteryValues.chargeCounter(null, null));
    }
    @Test public void configuredCounterUsesMicroAmpHours() {
        assertEquals(2500000L, BatteryValues.chargeCounter(5000, 50));
        assertEquals(0L, BatteryValues.chargeCounter(5000, -10));
        assertEquals(10000000L, BatteryValues.chargeCounter(20000, 120));
    }
    @Test public void propertyResultsPreservePrimitiveWrapperType() {
        assertEquals(Integer.class, BatteryValues.result(68, false).getClass());
        assertEquals(Long.class, BatteryValues.result(68, true).getClass());
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), BatteryValues.result(9000000000L, false));
        assertEquals(Long.valueOf(9000000000L), BatteryValues.result(9000000000L, true));
    }
}
