package mctmods.resourcedatapackloader.pack;

import com.google.gson.JsonObject;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class RDPLPackResources implements PackResources {
    protected final PackType type;

    protected RDPLPackResources(PackType type) { this.type = type; }

    protected static IoSupplier<InputStream> bytes(byte[] held) { return () -> new ByteArrayInputStream(held); }

    @Nonnull protected abstract String description();

    protected void extend(JsonObject json) {}

    @Nullable protected final IoSupplier<InputStream> metaFile(String name) { return PACK_META.equals(name) ? bytes(meta().getBytes(StandardCharsets.UTF_8)) : null; }

    @Override @Nullable public final <T> T getMetadataSection(@Nonnull MetadataSectionSerializer<T> serializer) throws IOException { return PackMeta.section(serializer, meta()); }

    private String meta() {
        JsonObject pack = new JsonObject();
        PackMeta.formats(pack, type);
        pack.addProperty("description", description());
        JsonObject json = new JsonObject();
        json.add("pack", pack);
        extend(json);
        return json.toString();
    }

    @Override public final boolean isBuiltin() { return false; }

    @Override public final void close() {}
}
