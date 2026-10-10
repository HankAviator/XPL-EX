package eu.faircode.xlua;

import org.junit.Test;
import org.luaj.vm2.compiler.LuaC;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.Assert.*;

/** Compile every shipped Lua asset without running hooks or touching Android. */
public class LuaAssetsSyntaxTest {
    @Test public void allShippedLuaAssetsCompile() throws Exception {
        List<String> errors = new ArrayList<>();
        int count = 0;
        try (Stream<Path> paths = Files.walk(Paths.get("src/main/assets"))) {
            for (Path path : (Iterable<Path>) paths.filter(p -> p.toString().endsWith(".lua"))::iterator) {
                count++;
                try (InputStream input = Files.newInputStream(path)) {
                    LuaC.instance.compile(input, path.toString());
                } catch (Exception error) {
                    errors.add(path + ": " + error);
                }
            }
        }
        assertTrue("No Lua assets inspected", count > 0);
        assertTrue(String.join("\n", errors), errors.isEmpty());
    }
}
