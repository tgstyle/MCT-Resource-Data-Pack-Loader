package mctmods.resourcedatapackloader.pack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.SharedConstants;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.server.packs.resources.ResourceMetadata;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;

public final class PackMeta {
    private PackMeta() {}

    public static void formats(JsonObject pack, PackType type) {
        PackFormat format = SharedConstants.getCurrentVersion().packVersion(type);
        pack.addProperty("pack_format", format.major());
        pack.remove("supported_formats");
        pack.add("min_format", array(format));
        pack.add("max_format", array(format));
    }

    private static JsonArray array(PackFormat format) {
        JsonArray array = new JsonArray();
        array.add(format.major());
        array.add(format.minor());
        return array;
    }

    @Nullable public static <T> T section(MetadataSectionType<T> type, String meta) throws IOException {
        try (InputStream stream = new ByteArrayInputStream(meta.getBytes(StandardCharsets.UTF_8))) { return ResourceMetadata.fromJsonStream(stream).getSection(type).orElse(null); }
    }
}
