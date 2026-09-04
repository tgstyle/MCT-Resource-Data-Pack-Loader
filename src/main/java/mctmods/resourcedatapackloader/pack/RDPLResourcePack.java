package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonObject;
import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.IoSupplier;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class RDPLResourcePack implements PackResources {
    public static final String ID = "RDPL";
    public static final String ID_OVERRIDING = "RDPL (overriding)";
    private static final String DEFAULT_DESCRIPTION = "Files loaded by Resource Data Pack Loader";
    private static final Set<String> TRACED = Collections.synchronizedSet(new HashSet<>());
    private final PackLocationInfo location;
    private final PackType type;
    private final boolean overriding;

    public RDPLResourcePack(PackLocationInfo location, PackType type, boolean overriding) {
        this.location = location;
        this.type = type;
        this.overriding = overriding;
    }

    public static String id(boolean overriding) { return overriding ? ID_OVERRIDING : ID; }

    public static Pack.ResourcesSupplier supplier(PackType type, boolean overriding) {
        return new Pack.ResourcesSupplier() {
            @Override @Nonnull public PackResources openPrimary(@Nonnull PackLocationInfo location) { return new RDPLResourcePack(location, type, overriding); }

            @Override @Nonnull public PackResources openFull(@Nonnull PackLocationInfo location, @Nonnull Pack.Metadata metadata) { return new RDPLResourcePack(location, type, overriding); }
        };
    }

    @Override @Nonnull public PackLocationInfo location() { return location; }

    @Override @Nullable public IoSupplier<InputStream> getRootResource(@Nonnull String... elements) {
        String name = String.join("/", elements);
        if (PACK_META.equals(name)) {
            byte[] meta = meta().getBytes(StandardCharsets.UTF_8);
            return () -> new ByteArrayInputStream(meta);
        }
        Path file = PackManager.get().packFile(name);
        return file == null ? null : IoSupplier.create(file);
    }

    @Override @Nullable public IoSupplier<InputStream> getResource(@Nonnull PackType asked, @Nonnull ResourceLocation location) {
        trace(location);
        if (!PackManager.get().existsRaw(asked, location.getNamespace(), location.getPath(), overriding)) { return null; }
        return supplier(asked, location.getNamespace(), location.getPath());
    }

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
            if (location != null) { out.accept(location, supplier(asked, namespace, path)); }
        });
    }

    @Override @Nonnull public Set<String> getNamespaces(@Nonnull PackType asked) { return PackManager.get().getNamespaces(asked, overriding); }

    @Override @Nullable public <T> T getMetadataSection(@Nonnull MetadataSectionSerializer<T> serializer) throws IOException {
        try (InputStream stream = new ByteArrayInputStream(meta().getBytes(StandardCharsets.UTF_8))) { return AbstractPackResources.getMetadataFromStream(serializer, stream); }
    }

    private String meta() {
        String description = PackManager.get().description();
        JsonObject pack = new JsonObject();
        pack.addProperty("pack_format", SharedConstants.getCurrentVersion().getPackVersion(type));
        pack.addProperty("description", description == null ? DEFAULT_DESCRIPTION : description);
        JsonObject json = new JsonObject();
        json.add("pack", pack);
        return json.toString();
    }

    @Override public void close() {}
}
