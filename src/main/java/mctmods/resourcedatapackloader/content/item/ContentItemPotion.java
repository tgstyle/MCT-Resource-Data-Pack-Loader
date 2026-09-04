package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentItem;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentItemPotion extends ItemPotion implements IContentItem {
    private final ItemDef def;
    @Nullable private List<PotionType> resolved;

    public ContentItemPotion(ItemDef def) {
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
