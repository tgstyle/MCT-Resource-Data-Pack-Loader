package mctmods.resourcedatapackloader.mixin.world.storage.loot;

import mctmods.resourcedatapackloader.common.util.ContentDisabled;

import java.util.List;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootTable.class) public abstract class MixinLootTable {
    @Inject(method = "generateLootForPools", at = @At("RETURN")) private void rdpl$stripDisabled(Random rand, LootContext context, CallbackInfoReturnable<List<ItemStack>> cir) { cir.getReturnValue().removeIf(ContentDisabled::disabled); }
}
