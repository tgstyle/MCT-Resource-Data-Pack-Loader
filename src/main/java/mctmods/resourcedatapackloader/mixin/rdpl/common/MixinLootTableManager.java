package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.world.PackLootTables;

import com.google.gson.Gson;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootTableManager.class) public abstract class MixinLootTableManager {
    @Shadow @Final private static Gson GSON_INSTANCE;
    @Unique private final PackLootTables<LootTable> rdpl$tables = new PackLootTables<>();

    @SuppressWarnings("ConstantConditions") @Inject(method = "getLootTableFromLocation", at = @At("HEAD"), cancellable = true) private void rdpl$serveFromPack(ResourceLocation ressources, CallbackInfoReturnable<LootTable> cir) {
        LootTable table = rdpl$tables.serve(ressources, ressources.getPath(), (location, contents) -> ForgeHooks.loadLootTable(GSON_INSTANCE, location, contents, false, (LootTableManager) (Object) this), "the built-in one");
        if (table != null) { cir.setReturnValue(table); }
    }

    @Inject(method = "reloadLootTables", at = @At("HEAD")) private void rdpl$invalidate(CallbackInfo ci) { rdpl$tables.clear(); }
}
