package mctmods.resourcedatapackloader.common.potions;

import mctmods.resourcedatapackloader.client.util.ContentPotionClient;
import mctmods.resourcedatapackloader.common.pack.definitions.effects.PotionDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.AttributeDef;
import mctmods.resourcedatapackloader.common.util.ContentAttributes;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    @Override public boolean isInstant() { return def.instant; }

    @Override public boolean hasStatusIcon() { return icon == null; }

    @Override @SideOnly(Side.CLIENT) public void renderInventoryEffect(@Nonnull PotionEffect effect, @Nonnull Gui gui, int x, int y, float z) { draw(x + 6, y + 7, 1.0F); }

    @Override @SideOnly(Side.CLIENT) public void renderHUDEffect(@Nonnull PotionEffect effect, @Nonnull Gui gui, int x, int y, float z, float alpha) { draw(x + 3, y + 3, alpha); }

    @SideOnly(Side.CLIENT) private void draw(int x, int y, float alpha) {
        ResourceLocation texture = icon;
        if (texture == null) { return; }
        ContentPotionClient.draw(texture, x, y, alpha);
    }
}
