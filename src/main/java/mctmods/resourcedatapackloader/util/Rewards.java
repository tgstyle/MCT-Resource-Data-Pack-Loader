package mctmods.resourcedatapackloader.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public final class Rewards {
    private Rewards() {}

    public static void loot(ServerLevel level, ServerPlayer player, String table, String asking) {
        if (table.isEmpty()) { return; }
        ResourceLocation id = ResourceLocation.tryParse(table);
        if (id == null) {
            ContentLog.LOGGER.error("{} names the loot table '{}', which is not a valid id, so nobody is handed loot", asking, table);
            return;
        }
        LootParams params = new LootParams.Builder(level).withParameter(LootContextParams.THIS_ENTITY, player).withParameter(LootContextParams.ORIGIN, player.position()).withLuck(player.getLuck()).create(LootContextParamSets.GIFT);
        for (ItemStack stack : level.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, id)).getRandomItems(params)) {
            if (!stack.isEmpty() && !player.getInventory().add(stack)) { player.drop(stack, false); }
        }
    }
}
