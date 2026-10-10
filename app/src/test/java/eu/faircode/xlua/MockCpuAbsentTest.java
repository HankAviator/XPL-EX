package eu.faircode.xlua;

import org.junit.Test;
import eu.faircode.xlua.api.cpu.MockCpu;
import eu.faircode.xlua.utilities.MockFileUtil;
import static org.junit.Assert.*;

public class MockCpuAbsentTest {
    @Test public void absentCpuDoesNotCrashOrFabricateAFile() {
        assertNull(MockFileUtil.generateFakeFile(null));
        assertNull(MockFileUtil.generateFakeFileDescriptor(null));
    }
    @Test public void blankCpuDoesNotReplaceProcCpuinfo() {
        MockCpu cpu = new MockCpu("test", "test", "test", "  ");
        assertNull(MockFileUtil.generateFakeFile(cpu));
        assertNull(MockFileUtil.generateFakeFileDescriptor(cpu));
    }
}
