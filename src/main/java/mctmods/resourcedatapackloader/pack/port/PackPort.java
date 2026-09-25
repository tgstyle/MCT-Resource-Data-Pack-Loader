package mctmods.resourcedatapackloader.pack.port;

import net.minecraft.server.packs.PackType;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;

public interface PackPort {
    String origin();

    PackType reads();

    void index(String namespace, List<String> realPaths);

    Map<PackType, Map<String, Set<String>>> exposed();

    @Nullable InputStream open(PackType type, String namespace, String path) throws IOException;

    void report();

    void writeVersion(ZipOutputStream out, String prefix) throws IOException;

    void closing();
}
