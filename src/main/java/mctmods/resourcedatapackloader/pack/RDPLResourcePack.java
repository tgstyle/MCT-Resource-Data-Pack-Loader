package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.Config;
import mctmods.resourcedatapackloader.core.MCTMixin;

import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;

@SideOnly(Side.CLIENT)
public final class RDPLResourcePack extends AbstractResourcePack {
    private static final String NAME = "RDPL";
    private static final String META = "pack.mcmeta";
    private static final String ICON = "pack.png";
    private static final String PREFIX = RDPLPack.ASSETS + "/";
    private static final String DEFAULT_META = "{\"pack\":{\"pack_format\":3,\"description\":\"Files loaded by Resource Data Pack Loader\"}}";
    private static final Set<String> TRACED = Collections.synchronizedSet(new HashSet<>());

    public RDPLResourcePack(File root) { super(root); }

    @Override @Nonnull public InputStream getInputStream(@Nonnull ResourceLocation location) throws IOException {
        InputStream stream = PackManager.get().openRaw(location.getNamespace(), location.getPath());
        if (stream == null) { throw new FileNotFoundException(location.toString()); }
        return stream;
    }

    @Override public boolean resourceExists(@Nonnull ResourceLocation location) {
        trace(location);
        return PackManager.get().existsRaw(location.getNamespace(), location.getPath());
    }

    private static void trace(ResourceLocation location) {
        if (!Config.settings.traceUnresolvedVariables) { return; }
        String path = location.getPath();
        if (path.indexOf('#') < 0) { return; }
        if (!TRACED.add(location.toString())) { return; }
        MCTMixin.LOGGER.warn("Something asked for {}, which is an unresolved model variable rather than a real file. The stack trace below shows what requested it.", location, new Throwable("requested here"));
    }

    @Override @Nonnull protected InputStream getInputStreamByName(@Nonnull String name) throws IOException {
        InputStream stream = openByName(name);
        if (stream == null) { throw new FileNotFoundException(name); }
        return stream;
    }

    @Override protected boolean hasResourceName(@Nonnull String name) {
        int split = split(name);
        if (split > 0) { return PackManager.get().existsRaw(name.substring(PREFIX.length(), split), name.substring(split + 1)); }
        return META.equals(name);
    }

    @Nullable private static InputStream openByName(String name) throws IOException {
        int split = split(name);
        if (split > 0) { return PackManager.get().openRaw(name.substring(PREFIX.length(), split), name.substring(split + 1)); }
        if (!META.equals(name)) { return null; }
        String contents = PackManager.get().readPackFile(META);
        if (contents == null) { contents = DEFAULT_META; }
        return new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8));
    }

    private static int split(String name) {
        if (!name.startsWith(PREFIX)) { return -1; }
        return name.indexOf('/', PREFIX.length());
    }

    @Override @Nonnull public Set<String> getResourceDomains() { return PackManager.get().getNamespaces(); }

    @Override @Nonnull public BufferedImage getPackImage() throws IOException {
        Path root = PackManager.get().getRoot();
        if (root != null && Files.isRegularFile(root.resolve(ICON))) {
            try (InputStream stream = Files.newInputStream(root.resolve(ICON))) {
                BufferedImage image = ImageIO.read(stream);
                if (image != null) { return image; }
            }
        }
        throw new FileNotFoundException(ICON);
    }

    @Override @Nonnull public String getPackName() { return NAME; }
}