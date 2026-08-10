package mctmods.resourcedatapackloader.loot;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.ArrayList;
import java.util.List;

public final class PlayerLoot {
    public static final String TABLE = "table";
    public static final String MODE = "mode";
    public static final String KEEP_INVENTORY = "rollOnKeepInventory";
    public static final String DROP_LOOSE = "dropLoose";
    private static final String ADD = "add";
    private static final String REPLACE = "replace";
    private static final String KEEP_INVENTORY_RULE = "keepInventory";
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static int generation = -1;

    private PlayerLoot() {}

    public static void reload() {
        ENTRIES.clear();
        generation = PackManager.get().getGeneration();
        if (!Config.data.playerLoot) { return; }

        PackManager.get().forEach(PackManager.PLAYER_LOOT, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            try { read(key, contents); }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in player loot {}, ignoring it", key, ex); }
        });

        if (!ENTRIES.isEmpty()) { Summary.info("loot.player", "Loaded " + ENTRIES.size() + " player loot table(s)"); }
    }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = new Gson().fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Player loot {} is empty, ignoring it", key);
            return;
        }

        String table = JsonUtils.getString(json, TABLE, "");
        if (table.isEmpty()) {
            ContentLog.LOGGER.error("Player loot {} has no table, ignoring it", key);
            return;
        }

        String mode = JsonUtils.getString(json, MODE, ADD);
        if (!mode.equals(ADD) && !mode.equals(REPLACE)) {
            ContentLog.LOGGER.error("Player loot {} has mode '{}', which is neither '{}' nor '{}', ignoring it", key, mode, ADD, REPLACE);
            return;
        }

        ENTRIES.add(new Entry(new ResourceLocation(table), mode.equals(REPLACE), JsonUtils.getBoolean(json, KEEP_INVENTORY, false), JsonUtils.getBoolean(json, DROP_LOOSE, false)));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDrops(PlayerDropsEvent event) {
        if (!Config.data.playerLoot) { return; }
        if (generation != PackManager.get().getGeneration()) { reload(); }
        if (ENTRIES.isEmpty()) { return; }

        EntityPlayer player = event.getEntityPlayer();
        if (player == null || !(player.world instanceof WorldServer)) { return; }

        WorldServer world = (WorldServer) player.world;
        boolean keeping = world.getGameRules().getBoolean(KEEP_INVENTORY_RULE) || player.isSpectator();
        List<Entry> rolling = new ArrayList<>();
        boolean replacing = false;
        for (Entry entry : ENTRIES) {
            if (keeping && !entry.keepInventory) { continue; }
            rolling.add(entry);
            replacing |= entry.replace;
        }
        if (rolling.isEmpty()) { return; }

        List<EntityItem> drops = event.getDrops();
        if (replacing) { drops.clear(); }

        EntityPlayer killer = killer(event);
        for (Entry entry : rolling) {
            LootTable table = world.getLootTableManager().getLootTableFromLocation(entry.table);
            LootContext.Builder builder = new LootContext.Builder(world).withLootedEntity(player).withDamageSource(event.getSource());
            if (killer != null) { builder.withPlayer(killer).withLuck(killer.getLuck()); }
            for (ItemStack stack : table.generateLootForPools(world.rand, builder.build())) {
                if (stack.isEmpty()) { continue; }
                EntityItem drop = new EntityItem(world, player.posX, player.posY, player.posZ, stack);
                drop.setDefaultPickupDelay();
                if (entry.loose) { world.spawnEntity(drop); }
                else { drops.add(drop); }
            }
        }
    }

    private static EntityPlayer killer(PlayerDropsEvent event) {
        if (!event.isRecentlyHit()) { return null; }
        Entity source = event.getSource().getTrueSource();
        return source instanceof EntityPlayer ? (EntityPlayer) source : null;
    }

    private static final class Entry {
        final ResourceLocation table;
        final boolean replace;
        final boolean keepInventory;
        final boolean loose;

        Entry(ResourceLocation table, boolean replace, boolean keepInventory, boolean loose) {
            this.table = table;
            this.replace = replace;
            this.keepInventory = keepInventory;
            this.loose = loose;
        }
    }
}
