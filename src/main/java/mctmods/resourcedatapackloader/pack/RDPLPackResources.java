package mctmods.resourcedatapackloader.pack;

import com.google.gson.JsonObject;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.IoSupplier;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class RDPLPackResources implements PackResources {
    private final PackLocationInfo location;
    protected final PackType type;

    protected RDPLPackResources(PackLocationInfo location, PackType type) {
        this.location = location;
        this.type = type;
    }

    protected static IoSupplier<InputStream> bytes(byte[] held) { return () -> new ByteArrayInputStream(held); }

    @Nonnull protected abstract String description();

    protected void extend(JsonObject json) {}

    @Nullable protected final IoSupplier<InputStream> metaFile(String name) { return PACK_META.equals(name) ? bytes(meta().getBytes(StandardCharsets.UTF_8)) : null; }

    @Override @Nonnull public final PackLocationInfo location() { return location; }

    @Override @Nullable public final <T> T getMetadataSection(@Nonnull MetadataSectionType<T> serializer) throws IOException { return PackMeta.section(serializer, meta()); }

    private String meta() {
        JsonObject pack = new JsonObject();
        PackMeta.formats(pack, type);
        pack.addProperty("description", description());
        JsonObject json = new JsonObject();
        json.add("pack", pack);
        extend(json);
        return json.toString();
    }

    @Override public final void close() {}
}
