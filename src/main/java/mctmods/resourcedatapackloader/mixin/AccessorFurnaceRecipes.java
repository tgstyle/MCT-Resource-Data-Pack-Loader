package mctmods.resourcedatapackloader.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;

@Mixin(FurnaceRecipes.class)
public interface AccessorFurnaceRecipes {
    @Accessor("experienceList") Map<ItemStack, Float> rdpl$getExperienceList();
}
