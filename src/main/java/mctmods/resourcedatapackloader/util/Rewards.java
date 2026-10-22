package mctmods.resourcedatapackloader.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.items.ItemHandlerHelper;

public final class Rewards {
    private Rewards() {}

    public static void loot(WorldServer world, EntityPlayerMP player, String table) {
        if (table.isEmpty()) { return; }
        LootTable rolled = world.getLootTableManager().getLootTableFromLocation(new ResourceLocation(table));
        LootContext context = new LootContext.Builder(world).withLootedEntity(player).withPlayer(player).withLuck(player.getLuck()).build();
        for (ItemStack stack : rolled.generateLootForPools(world.rand, context)) {
            if (!stack.isEmpty()) { ItemHandlerHelper.giveItemToPlayer(player, stack); }
        }
    }
}
