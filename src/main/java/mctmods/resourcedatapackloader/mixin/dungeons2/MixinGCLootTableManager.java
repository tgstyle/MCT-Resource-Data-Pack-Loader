package mctmods.resourcedatapackloader.mixin.dungeons2;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
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
import java.util.HashMap;
import java.util.Map;

@Mixin(value = LootTableManager.class, remap = false) @SuppressWarnings("deprecation") public abstract class MixinGCLootTableManager {
    @Shadow @Final private static Gson GSON_INSTANCE;
    @Unique private Map<ResourceLocation, LootTable> rdpl$cache;

    @SuppressWarnings("ConstantConditions") @Inject(method = "getLootTableFromLocation", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$serveFromPack(ResourceLocation location, CallbackInfoReturnable<LootTable> cir) {
        PackManager manager = PackManager.get();
        if (manager.isEmpty()) { return; }
        if (rdpl$cache == null) { rdpl$cache = new HashMap<>(); }
        LootTable cached = rdpl$cache.get(location);
        if (cached != null) {
            cir.setReturnValue(cached);
            return;
        }
        String path = location.getPath();
        if (path.startsWith(PackManager.LOOT_TABLES + "/")) { path = path.substring(PackManager.LOOT_TABLES.length() + 1); }
        String contents = manager.read(location.getNamespace(), path, PackManager.LOOT_TABLES, PackManager.JSON);
        if (contents == null) { return; }
        try {
            LootTable table = LootTableManager.loadLootTable(GSON_INSTANCE, location, contents, true, (LootTableManager) (Object) this);
            if (table == null) { return; }
            rdpl$cache.put(location, table);
            cir.setReturnValue(table);
        }
        catch (IllegalArgumentException | JsonParseException ex) {
            ContentLog.LOGGER.error("Parsing error in loot table {}, falling back to the one on disk", location, ex);
        }
    }

    @Inject(method = "reloadLootTables", at = @At("HEAD"), remap = false) private void rdpl$invalidate(CallbackInfo ci) {
        if (rdpl$cache != null) { rdpl$cache.clear(); }
    }
}
