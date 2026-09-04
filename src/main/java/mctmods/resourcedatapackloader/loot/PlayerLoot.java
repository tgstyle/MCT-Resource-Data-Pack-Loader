package mctmods.resourcedatapackloader.loot;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

public final class PlayerLoot {
    public static final String TABLE = "table";
    public static final String MODE = "mode";
    public static final String KEEP_INVENTORY = "rollOnKeepInventory";
    public static final String DROP_LOOSE = "dropLoose";
    private static final String ADD = "add";
    private static final String REPLACE = "replace";
    private static final Gson GSON = new GsonBuilder().create();
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static int generation = -1;

    private PlayerLoot() {}

    public static void reload() {
        ENTRIES.clear();
        generation = PackManager.get().getGeneration();
        if (!Config.data.playerLoot()) { return; }
        PackManager.get().forEach(PackManager.PLAYER_LOOT, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = ResourceLocation.fromNamespaceAndPath(namespace, path);
            try { read(key, contents); }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in player loot {}, ignoring it", key, ex); }
        });
        if (!ENTRIES.isEmpty()) { Summary.info("loot.player", "Loaded " + ENTRIES.size() + " player loot table(s)"); }
    }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Player loot {} is empty, ignoring it", key);
            return;
        }
        String table = GsonHelper.getAsString(json, TABLE, "");
        if (table.isEmpty()) {
            ContentLog.LOGGER.error("Player loot {} has no table, ignoring it", key);
            return;
        }
        String mode = GsonHelper.getAsString(json, MODE, ADD);
        if (!mode.equals(ADD) && !mode.equals(REPLACE)) {
            ContentLog.LOGGER.error("Player loot {} has mode '{}', which is neither '{}' nor '{}', ignoring it", key, mode, ADD, REPLACE);
            return;
        }
        ENTRIES.add(new Entry(ResourceLocation.parse(table), mode.equals(REPLACE), GsonHelper.getAsBoolean(json, KEEP_INVENTORY, false), GsonHelper.getAsBoolean(json, DROP_LOOSE, false)));
    }

    public static void onDrops(LivingDropsEvent event) {
        if (!Config.data.playerLoot()) { return; }
        if (generation != PackManager.get().getGeneration()) { reload(); }
        if (ENTRIES.isEmpty() || !(event.getEntity() instanceof ServerPlayer player)) { return; }
        ServerLevel level = player.serverLevel();
        boolean keeping = level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || player.isSpectator();
        List<Entry> rolling = new ArrayList<>();
        boolean replacing = false;
        for (Entry entry : ENTRIES) {
            if (keeping && !entry.keepInventory()) { continue; }
            rolling.add(entry);
            replacing |= entry.replace();
        }
        if (rolling.isEmpty()) { return; }
        Collection<ItemEntity> drops = event.getDrops();
        if (replacing) { drops.clear(); }
        DamageSource source = event.getSource();
        Player killer = killer(event);
        for (Entry entry : rolling) {
            LootTable table = level.getServer().getLootData().getLootTable(entry.table());
            LootParams.Builder builder = new LootParams.Builder(level)
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .withParameter(LootContextParams.ORIGIN, player.position())
                    .withParameter(LootContextParams.DAMAGE_SOURCE, source)
                    .withOptionalParameter(LootContextParams.KILLER_ENTITY, source.getEntity())
                    .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, source.getDirectEntity());
            if (killer != null) { builder = builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, killer).withLuck(killer.getLuck()); }
            for (ItemStack stack : table.getRandomItems(builder.create(LootContextParamSets.ENTITY))) {
                if (stack.isEmpty()) { continue; }
                ItemEntity drop = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), stack);
                drop.setDefaultPickUpDelay();
                if (entry.loose()) { level.addFreshEntity(drop); }
                else { drops.add(drop); }
            }
        }
    }

    @Nullable private static Player killer(LivingDropsEvent event) {
        if (!event.isRecentlyHit()) { return null; }
        return event.getSource().getEntity() instanceof Player player ? player : null;
    }

    private record Entry(ResourceLocation table, boolean replace, boolean keepInventory, boolean loose) {}
}
