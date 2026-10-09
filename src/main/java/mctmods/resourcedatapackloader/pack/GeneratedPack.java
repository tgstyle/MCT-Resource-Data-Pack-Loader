package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.compat.LineCompat;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.IoSupplier;
import java.io.InputStream;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class GeneratedPack extends RDPLPackResources {
    public static final String ID = "RDPL (generated)";
    private static final String DESCRIPTION = "Blockstates, models, loot tables and tags generated for pack content";

    public GeneratedPack(PackLocationInfo location, PackType type) { super(location, type); }

    public static Pack.ResourcesSupplier supplier(PackType type) { return LineCompat.packSupplier(location -> new GeneratedPack(location, type)); }

    @Override @Nonnull protected String description() { return DESCRIPTION; }

    @Override @Nullable public IoSupplier<InputStream> getRootResource(@Nonnull String... elements) { return metaFile(String.join("/", elements)); }

    @Override @Nullable public IoSupplier<InputStream> getResource(@Nonnull PackType asked, @Nonnull Identifier location) {
        byte[] held = GeneratedResources.get(asked, location.getNamespace(), location.getPath());
        return held == null ? null : bytes(held);
    }

    @Override public void listResources(@Nonnull PackType asked, @Nonnull String namespace, @Nonnull String prefix, @Nonnull ResourceOutput out) {
        GeneratedResources.list(asked, namespace, prefix, path -> {
            Identifier location = Identifier.tryBuild(namespace, path);
            byte[] held = GeneratedResources.get(asked, namespace, path);
            if (location != null && held != null) { out.accept(location, bytes(held)); }
        });
    }

    @Override @Nonnull public Set<String> getNamespaces(@Nonnull PackType asked) { return GeneratedResources.namespaces(asked); }
}
