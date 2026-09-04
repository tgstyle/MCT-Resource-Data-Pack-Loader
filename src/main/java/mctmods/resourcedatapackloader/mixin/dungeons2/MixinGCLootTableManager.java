package mctmods.resourcedatapackloader.mixin.dungeons2;

import mctmods.resourcedatapackloader.util.world.PackLootTables;

import com.google.gson.Gson;
import com.someguyssoftware.gottschcore.loot.LootTable;
import com.someguyssoftware.gottschcore.loot.LootTableManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LootTableManager.class, remap = false) @SuppressWarnings("deprecation") public abstract class MixinGCLootTableManager {
    @Shadow @Final private static Gson GSON_INSTANCE;
    @Unique private final PackLootTables<LootTable> rdpl$tables = new PackLootTables<>();

    @SuppressWarnings("ConstantConditions") @Inject(method = "getLootTableFromLocation", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$serveFromPack(ResourceLocation location, CallbackInfoReturnable<LootTable> cir) {
        LootTable table = rdpl$tables.serve(location, PackLootTables.tablePath(location), (at, contents) -> LootTableManager.loadLootTable(GSON_INSTANCE, at, contents, true, (LootTableManager) (Object) this), "the one on disk");
        if (table != null) { cir.setReturnValue(table); }
    }

    @Inject(method = "reloadLootTables", at = @At("HEAD"), remap = false) private void rdpl$invalidate(CallbackInfo ci) { rdpl$tables.clear(); }
}
