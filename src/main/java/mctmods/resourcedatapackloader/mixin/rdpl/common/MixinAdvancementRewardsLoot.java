package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentLootSwitch;

import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AdvancementRewards.class) public abstract class MixinAdvancementRewardsLoot {
    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/loot/LootTableManager;getLootTableFromLocation(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/world/storage/loot/LootTable;")) private LootTable rdpl$swapLoot(LootTableManager manager, ResourceLocation ressources, EntityPlayerMP player) { return manager.getLootTableFromLocation(ContentLootSwitch.swap(player.world, new BlockPos(player), ressources)); }
}
