package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.loot.LootInjections;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
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
import java.util.HashMap;
import java.util.Map;

@Mixin(LootTableManager.class)
public abstract class MixinLootTableManager {
    @Shadow @Final
    private static Gson GSON_INSTANCE;
    @Unique
    private Map<ResourceLocation, LootTable> rdpl$cache;

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "getLootTableFromLocation", at = @At("RETURN"))
    private void rdpl$inject(ResourceLocation ressources, CallbackInfoReturnable<LootTable> cir) {
        LootTable table = cir.getReturnValue();
        if (table == null || table == LootTable.EMPTY_LOOT_TABLE) { return; }
        LootInjections.apply(ressources, table, GSON_INSTANCE);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "getLootTableFromLocation", at = @At("HEAD"), cancellable = true)
    private void rdpl$serveFromPack(ResourceLocation ressources, CallbackInfoReturnable<LootTable> cir) {
        PackManager manager = PackManager.get();
        if (manager.isEmpty()) { return; }
        if (rdpl$cache == null) { rdpl$cache = new HashMap<>(); }
        LootTable cached = rdpl$cache.get(ressources);
        if (cached != null) {
            cir.setReturnValue(cached);
            return;
        }
        String contents = manager.read(ressources.getNamespace(), ressources.getPath(), PackManager.LOOT_TABLES, PackManager.JSON);
        if (contents == null) { return; }
        try {
            LootTable table = ForgeHooks.loadLootTable(GSON_INSTANCE, ressources, contents, false, (LootTableManager) (Object) this);
            if (table == null) { return; }
            rdpl$cache.put(ressources, table);
            cir.setReturnValue(table);
        }
        catch (IllegalArgumentException | JsonParseException ex) {
            ContentLog.LOGGER.error("Parsing error in loot table {}, falling back to the built-in one", ressources, ex);
        }
    }

    @Inject(method = "reloadLootTables", at = @At("HEAD"))
    private void rdpl$invalidate(CallbackInfo ci) {
        if (rdpl$cache != null) { rdpl$cache.clear(); }
    }
}
