package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.def.AttributeDef;
import mctmods.resourcedatapackloader.content.def.PotionDef;
import mctmods.resourcedatapackloader.content.util.ContentAttributes;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentPotion extends Potion {
    private final PotionDef def;
    @Nullable private final ResourceLocation icon;

    public ContentPotion(PotionDef def) {
        super(def.badEffect, def.liquidColor);
        this.def = def;
        this.icon = def.iconTexture.isEmpty() ? null : new ResourceLocation(def.iconTexture);

        setRegistryName(def.registryName);
        setPotionName(def.name);
        setIconIndex(def.iconX, def.iconY);
        setEffectiveness(def.effectiveness);
        if (def.beneficial) { setBeneficial(); }

        for (AttributeDef modifier : def.attributes) {
            IAttribute attribute = ContentAttributes.find(modifier.attribute, def.registryName);
            if (attribute == null) { continue; }

            try { registerPotionAttributeModifier(attribute, modifier.uuid, modifier.amount, modifier.operation); }
            catch (IllegalArgumentException ex) { ContentLog.LOGGER.error("Attribute modifier for {} has an unusable uuid '{}', skipping it", def.registryName, modifier.uuid); }
        }
    }

    public PotionDef getDef() { return def; }

    @Override public boolean isInstant() { return def.instant; }

    @Override public boolean hasStatusIcon() { return icon == null; }

    @Override @SideOnly(Side.CLIENT) public void renderInventoryEffect(@Nonnull PotionEffect effect, @Nonnull Gui gui, int x, int y, float z) { draw(x + 6, y + 7, 1.0F); }

    @Override @SideOnly(Side.CLIENT) public void renderHUDEffect(@Nonnull PotionEffect effect, @Nonnull Gui gui, int x, int y, float z, float alpha) { draw(x + 3, y + 3, alpha); }

    @SideOnly(Side.CLIENT)
    private void draw(int x, int y, float alpha) {
        ResourceLocation texture = icon;
        if (texture == null) { return; }

        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 18, 18, 18.0F, 18.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
