package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PotionBrewing.class) public interface IPotionBrewing {
    @Invoker("addMix") static void rdpl$addMix(Potion from, Item ingredient, Potion to) { throw new AssertionError(); }

    @Invoker("addContainer") static void rdpl$addContainer(Item container) { throw new AssertionError(); }
}
