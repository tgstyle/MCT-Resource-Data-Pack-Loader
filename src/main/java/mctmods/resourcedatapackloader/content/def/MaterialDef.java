package mctmods.resourcedatapackloader.content.def;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import java.util.List;
import javax.annotation.Nullable;

public final class MaterialDef {
    public final ResourceLocation registryName;
    public final String name;
    public final int harvestLevel;
    public final int durability;
    public final float efficiency;
    public final float damage;
    public final int enchantability;
    public final int[] reduction;
    public final float toughness;
    public final String equipSound;
    public final String armorTexture;
    public final String repairItem;
    public final List<String> requires;
    @Nullable private Item.ToolMaterial tool;
    @Nullable private ItemArmor.ArmorMaterial armor;
    private ItemStack repair = ItemStack.EMPTY;

    public MaterialDef(ResourceLocation registryName, String name, int harvestLevel, int durability, float efficiency, float damage, int enchantability, int[] reduction, float toughness, String equipSound, String armorTexture, String repairItem, List<String> requires) {
        this.registryName = registryName;
        this.name = name;
        this.harvestLevel = harvestLevel;
        this.durability = durability;
        this.efficiency = efficiency;
        this.damage = damage;
        this.enchantability = enchantability;
        this.reduction = reduction;
        this.toughness = toughness;
        this.equipSound = equipSound;
        this.armorTexture = armorTexture;
        this.repairItem = repairItem;
        this.requires = requires;
    }

    public void resolve(@Nullable Item.ToolMaterial tool, @Nullable ItemArmor.ArmorMaterial armor, ItemStack repair) {
        this.tool = tool;
        this.armor = armor;
        this.repair = repair;
    }

    @Nullable public Item.ToolMaterial getTool() { return tool; }

    @Nullable public ItemArmor.ArmorMaterial getArmor() { return armor; }

    public ItemStack getRepair() { return repair; }
}
