package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.blocks.ContentSetup;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ContentPotionItem extends ItemPotion implements IContentItem {
    private final ItemDef def;
    @Nullable private List<PotionType> resolved;

    public ContentPotionItem(ItemDef def) {
        this.def = def;
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName.toString());
        ContentSetup.apply(this, def.creativeTab);
    }

    @Override public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) { return; }
        for (PotionType type : types()) { items.add(PotionUtils.addPotionToItemStack(new ItemStack(this), type)); }
    }

    private List<PotionType> types() {
        if (resolved != null) { return resolved; }
        resolved = resolve();
        return resolved;
    }

    private List<PotionType> resolve() {
        List<PotionType> found = new ArrayList<>();
        if (def.potionTypes.isEmpty()) {
            for (PotionType type : ForgeRegistries.POTION_TYPES) {
                ResourceLocation name = type.getRegistryName();
                if (name != null && name.getNamespace().equals(def.registryName.getNamespace())) { found.add(type); }
            }
            return found;
        }
        for (String name : def.potionTypes) {
            ResourceLocation location = new ResourceLocation(name);
            if (!ForgeRegistries.POTION_TYPES.containsKey(location)) {
                ContentLog.LOGGER.warn("Potion bottle {} names potion type '{}', which is not registered, so it is left out. If the pack does define it, check whether content.potions is off", def.registryName, name);
                continue;
            }
            found.add(ForgeRegistries.POTION_TYPES.getValue(location));
        }
        return found;
    }
}
