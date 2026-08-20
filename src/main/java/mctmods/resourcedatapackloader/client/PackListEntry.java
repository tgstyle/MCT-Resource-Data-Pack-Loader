package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.pack.RDPLResourcePack;

import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import javax.annotation.Nonnull;
import java.io.IOException;

@SideOnly(Side.CLIENT) public final class PackListEntry extends ResourcePackListEntry {
    private static final ResourceLocation UNKNOWN_PACK = new ResourceLocation("textures/misc/unknown_pack.png");
    private static ResourceLocation normalIcon;
    private static ResourceLocation overrideIcon;
    private final RDPLResourcePack pack;
    private final boolean overriding;
    private final String name;
    private final String description;

    public PackListEntry(GuiScreenResourcePacks screen, RDPLResourcePack pack, boolean overriding) {
        super(screen);
        this.pack = pack;
        this.overriding = overriding;
        this.name = pack.getPackName();
        this.description = I18n.format(overriding ? "rdpl.gui.packList.override" : "rdpl.gui.packList.normal");
    }

    @Override @Nonnull protected String getResourcePackName() { return name; }

    @Override @Nonnull protected String getResourcePackDescription() { return description; }

    @Override protected int getResourcePackFormat() { return 3; }

    @Override protected void bindResourcePackIcon() { mc.getTextureManager().bindTexture(icon()); }

    @Override protected boolean showHoverOverlay() { return false; }

    @Override protected boolean canMoveRight() { return false; }

    @Override protected boolean canMoveLeft() { return false; }

    @Override protected boolean canMoveUp() { return false; }

    @Override protected boolean canMoveDown() { return false; }

    @Override public boolean isServerPack() { return overriding; }

    private ResourceLocation icon() {
        if (overriding) {
            if (overrideIcon == null) { overrideIcon = load(); }
            return overrideIcon;
        }
        if (normalIcon == null) { normalIcon = load(); }
        return normalIcon;
    }

    private ResourceLocation load() {
        try { return mc.getTextureManager().getDynamicTextureLocation("rdplpackicon", new DynamicTexture(pack.getPackImage())); }
        catch (IOException missing) { return UNKNOWN_PACK; }
    }
}
