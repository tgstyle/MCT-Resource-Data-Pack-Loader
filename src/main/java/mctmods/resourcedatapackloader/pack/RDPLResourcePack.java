package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class RDPLResourcePack extends RDPLPackResources {
    public static final String ID = "RDPL";
    public static final String ID_OVERRIDING = "RDPL (overriding)";
    private static final String DEFAULT_DESCRIPTION = "Files loaded by Resource Data Pack Loader";
    private static final Set<String> TRACED = Collections.synchronizedSet(new HashSet<>());
    private final boolean overriding;

    public RDPLResourcePack(PackType type, boolean overriding) {
        super(type);
        this.overriding = overriding;
    }

    public static String id(boolean overriding) { return overriding ? ID_OVERRIDING : ID; }

    @Override @Nonnull public String packId() { return id(overriding); }

    @Override @Nonnull protected String description() {
        String description = PackManager.get().description();
        return description == null ? DEFAULT_DESCRIPTION : description;
    }

    @Override protected void extend(JsonObject json) {
        JsonObject language = type == PackType.CLIENT_RESOURCES ? PackManager.get().language() : null;
        if (language != null) { json.add("language", language); }
    }

    @Override @Nullable public IoSupplier<InputStream> getRootResource(@Nonnull String... elements) {
        String name = String.join("/", elements);
        IoSupplier<InputStream> meta = metaFile(name);
        if (meta != null) { return meta; }
        Path file = PackManager.get().packFile(name);
        if (file != null) { return IoSupplier.create(file); }
        byte[] icon = FallbackIcon.FILE.equals(name) ? FallbackIcon.bytes() : null;
        return icon == null ? null : bytes(icon);
    }

    @Override @Nullable public IoSupplier<InputStream> getResource(@Nonnull PackType asked, @Nonnull ResourceLocation location) {
        trace(location);
        if (withheld(asked, location.getPath()) || !PackManager.get().existsRaw(asked, location.getNamespace(), location.getPath(), overriding)) { return null; }
        return supplier(asked, location.getNamespace(), location.getPath());
    }

    private static boolean withheld(PackType asked, String path) { return asked == PackType.SERVER_DATA && path.startsWith(PackManager.FUNCTIONS + "/") && Config.data.functionsOff(); }

    private IoSupplier<InputStream> supplier(PackType asked, String namespace, String path) {
        return () -> {
            InputStream stream = PackManager.get().openRaw(asked, namespace, path, overriding);
            if (stream == null) { throw new FileNotFoundException(asked.getDirectory() + "/" + namespace + "/" + path); }
            return stream;
        };
    }

    private static void trace(ResourceLocation location) {
        if (!Config.packs.traceUnresolvedVariables()) { return; }
        String path = location.getPath();
        if (path.indexOf('#') < 0) { return; }
        if (!TRACED.add(location.toString())) { return; }
        ContentLog.LOGGER.warn("Something asked for {}, which is an unresolved model variable rather than a real file. The stack trace below shows what requested it.", location, new Throwable("requested here"));
    }

    @Override public void listResources(@Nonnull PackType asked, @Nonnull String namespace, @Nonnull String prefix, @Nonnull ResourceOutput out) {
        PackManager.get().list(asked, namespace, overriding, prefix, path -> {
            ResourceLocation location = ResourceLocation.tryBuild(namespace, path);
            if (location != null && !withheld(asked, path)) { out.accept(location, supplier(asked, namespace, path)); }
        });
    }

    @Override @Nonnull public Set<String> getNamespaces(@Nonnull PackType asked) { return PackManager.get().getNamespaces(asked, overriding); }
}
