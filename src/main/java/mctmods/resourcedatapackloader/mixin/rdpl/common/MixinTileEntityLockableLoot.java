package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentLootSwitch;

import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityLockableLoot.class) public abstract class MixinTileEntityLockableLoot {
    @Redirect(method = "fillWithLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/loot/LootTableManager;getLootTableFromLocation(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/world/storage/loot/LootTable;")) private LootTable rdpl$swapLoot(LootTableManager manager, ResourceLocation ressources) {
        TileEntityLockableLoot self = (TileEntityLockableLoot) (Object) this;
        return manager.getLootTableFromLocation(ContentLootSwitch.swap(self.getWorld(), self.getPos(), ressources));
    }
}
