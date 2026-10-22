package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentLootSwitch;

import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityFishHook.class) public abstract class MixinEntityFishHook {
    @Redirect(method = "handleHookRetraction", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/loot/LootTableManager;getLootTableFromLocation(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/world/storage/loot/LootTable;")) private LootTable rdpl$swapLoot(LootTableManager manager, ResourceLocation ressources) {
        EntityFishHook self = (EntityFishHook) (Object) this;
        return manager.getLootTableFromLocation(ContentLootSwitch.swap(self.world, new BlockPos(self), ressources));
    }
}
