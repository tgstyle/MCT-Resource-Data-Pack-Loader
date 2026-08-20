package mctmods.resourcedatapackloader.mixin.rdpl.common;

import com.google.gson.Gson;
import net.minecraft.world.storage.loot.LootTableManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootTableManager.class) public interface ILootTableManager {
    @Accessor("GSON_INSTANCE") static Gson rdpl$gson() { throw new AssertionError(); }
}
