package eu.faircode.xlua.x.file;

import org.junit.Test;
import java.io.File;
import java.io.IOException;
import static org.junit.Assert.*;

public class ZipEntrySafetyTest {
    @org.junit.Rule public org.junit.rules.TemporaryFolder temporary = new org.junit.rules.TemporaryFolder();
    private final File root = new File(System.getProperty("java.io.tmpdir"), "xpl-zip-test");

    @Test public void acceptsNestedEntryWithoutDirectoryRecord() throws Exception {
        assertEquals(new File(root, "a/b.txt").getCanonicalFile(),
                FileCompressionUtils.resolveEntry(root, "a/b.txt"));
    }

    @Test public void rejectsTraversalAndSiblingPrefix() throws Exception {
        for (String path : new String[]{"../escape", "../xpl-zip-test-other/file", "a/../../escape", ".", "/absolute"}) {
            try {
                FileCompressionUtils.resolveEntry(root, path);
                fail("Accepted unsafe entry: " + path);
            } catch (IOException expected) {
                assertTrue(expected.getMessage().contains("escapes"));
            }
        }
    }

    @Test public void extractsNestedFilesAndExistingDirectories() throws Exception {
        File archive = temporary.newFile("input.zip");
        File output = temporary.newFolder("output");
        assertTrue(new File(output, "existing").mkdir());
        try (java.util.zip.ZipOutputStream zip = new java.util.zip.ZipOutputStream(new java.io.FileOutputStream(archive))) {
            zip.putNextEntry(new java.util.zip.ZipEntry("existing/"));
            zip.closeEntry();
            zip.putNextEntry(new java.util.zip.ZipEntry("nested/file.txt"));
            zip.write("payload".getBytes(java.nio.charset.StandardCharsets.UTF_8));
            zip.closeEntry();
        }
        FileCompressionUtils.decompressZip(archive, output);
        assertEquals("payload", new String(java.nio.file.Files.readAllBytes(
                new File(output, "nested/file.txt").toPath()), java.nio.charset.StandardCharsets.UTF_8));
    }
}
