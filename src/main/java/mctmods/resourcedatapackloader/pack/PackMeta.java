package mctmods.resourcedatapackloader.pack;

import com.google.gson.JsonObject;
import net.minecraft.SharedConstants;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;

public final class PackMeta {
    private PackMeta() {}

    public static void formats(JsonObject pack, PackType type) { pack.addProperty("pack_format", SharedConstants.getCurrentVersion().getPackVersion(type)); }

    @Nullable public static <T> T section(MetadataSectionSerializer<T> serializer, String meta) throws IOException {
        try (InputStream stream = new ByteArrayInputStream(meta.getBytes(StandardCharsets.UTF_8))) { return AbstractPackResources.getMetadataFromStream(serializer, stream); }
    }
}
