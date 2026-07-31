package mctmods.resourcedatapackloader.content.def;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import java.util.List;
import java.util.Map;

public final class ItemDef {
    public final ResourceLocation registryName;
    public final String type;
    public final String creativeTab;
    public final boolean alwaysEdible;
    public final Map<Integer, ItemVariant> byMeta;
    public final List<ItemVariant> visible;
    public final List<String> requires;
    public final int useDuration;
    public final boolean eat;
    public final String container;
    public final String material;
    public final String toolClass;
    public final String slot;
    public final String crop;
    public final String soil;
    public final List<String> potionTypes;
    private ItemStack resolvedContainer = ItemStack.EMPTY;

    public ItemDef(ResourceLocation registryName, String type, String creativeTab, boolean alwaysEdible, Map<Integer, ItemVariant> byMeta, List<ItemVariant> visible, List<String> requires, int useDuration, boolean eat, String container, String material, String toolClass, String slot, String crop, String soil, List<String> potionTypes) {
        this.registryName = registryName;
        this.type = type;
        this.creativeTab = creativeTab;
        this.alwaysEdible = alwaysEdible;
        this.byMeta = byMeta;
        this.visible = visible;
        this.requires = requires;
        this.useDuration = useDuration;
        this.eat = eat;
        this.container = container;
        this.material = material;
        this.toolClass = toolClass;
        this.slot = slot;
        this.crop = crop;
        this.soil = soil;
        this.potionTypes = potionTypes;
    }

    public void resolveContainer(ItemStack stack) { this.resolvedContainer = stack; }

    public ItemStack getResolvedContainer() { return resolvedContainer; }
}
