package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentLootSwitch;

import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityMinecartContainer.class) public abstract class MixinEntityMinecartContainer {
    @Redirect(method = "addLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/loot/LootTableManager;getLootTableFromLocation(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/world/storage/loot/LootTable;")) private LootTable rdpl$swapLoot(LootTableManager manager, ResourceLocation ressources) {
        EntityMinecartContainer self = (EntityMinecartContainer) (Object) this;
        return manager.getLootTableFromLocation(ContentLootSwitch.swap(self.world, new BlockPos(self), ressources));
    }
}
