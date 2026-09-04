package mctmods.resourcedatapackloader.pack;

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
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class GeneratedPack implements PackResources {
    public static final String ID = "RDPL (generated)";
    private static final String DESCRIPTION = "Blockstates, models, loot tables and tags generated for pack content";
    private final PackLocationInfo location;
    private final PackType type;

    public GeneratedPack(PackLocationInfo location, PackType type) {
        this.location = location;
        this.type = type;
    }

    public static Pack.ResourcesSupplier supplier(PackType type) {
        return new Pack.ResourcesSupplier() {
            @Override @Nonnull public PackResources openPrimary(@Nonnull PackLocationInfo location) { return new GeneratedPack(location, type); }

            @Override @Nonnull public PackResources openFull(@Nonnull PackLocationInfo location, @Nonnull Pack.Metadata metadata) { return new GeneratedPack(location, type); }
        };
    }

    @Override @Nonnull public PackLocationInfo location() { return location; }

    @Override @Nullable public IoSupplier<InputStream> getRootResource(@Nonnull String... elements) {
        if (!PACK_META.equals(String.join("/", elements))) { return null; }
        byte[] meta = meta().getBytes(StandardCharsets.UTF_8);
        return () -> new ByteArrayInputStream(meta);
    }

    @Override @Nullable public IoSupplier<InputStream> getResource(@Nonnull PackType asked, @Nonnull ResourceLocation location) {
        byte[] held = GeneratedResources.get(asked, location.getNamespace(), location.getPath());
        return held == null ? null : () -> new ByteArrayInputStream(held);
    }

    @Override public void listResources(@Nonnull PackType asked, @Nonnull String namespace, @Nonnull String prefix, @Nonnull ResourceOutput out) {
        GeneratedResources.list(asked, namespace, prefix, path -> {
            ResourceLocation location = ResourceLocation.tryBuild(namespace, path);
            byte[] held = GeneratedResources.get(asked, namespace, path);
            if (location != null && held != null) { out.accept(location, () -> new ByteArrayInputStream(held)); }
        });
    }

    @Override @Nonnull public Set<String> getNamespaces(@Nonnull PackType asked) { return GeneratedResources.namespaces(asked); }

    @Override @Nullable public <T> T getMetadataSection(@Nonnull MetadataSectionSerializer<T> serializer) throws IOException {
        try (InputStream stream = new ByteArrayInputStream(meta().getBytes(StandardCharsets.UTF_8))) { return AbstractPackResources.getMetadataFromStream(serializer, stream); }
    }

    private String meta() {
        JsonObject pack = new JsonObject();
        pack.addProperty("pack_format", SharedConstants.getCurrentVersion().getPackVersion(type));
        pack.addProperty("description", DESCRIPTION);
        JsonObject json = new JsonObject();
        json.add("pack", pack);
        return json.toString();
    }

    @Override public void close() {}
}
