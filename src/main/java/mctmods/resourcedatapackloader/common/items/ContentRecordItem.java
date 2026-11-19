package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.blocks.ContentSetup;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.RecordDef;

import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;

public class ContentRecordItem extends ItemRecord implements IContentItem {
    private final ItemDef def;

    public ContentRecordItem(ItemDef def, RecordDef record) {
        super(def.registryName.getNamespace() + "." + def.registryName.getPath(), new SoundEvent(record.sound));
        this.def = def;
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName.toString());
        setHasSubtypes(true);
        ContentSetup.apply(this, def.creativeTab);
    }

    @Override public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) { if (isInCreativeTab(tab)) { ItemVariants.subItems(this, def, list); } }

    @Override @Nonnull public String getTranslationKey(@Nonnull ItemStack stack) { return ItemVariants.key(this, def, stack); }

    @Override public int getMetadata(int damage) { return damage; }

    public int comparator() { return def.record == null ? -1 : def.record.comparator; }
}
