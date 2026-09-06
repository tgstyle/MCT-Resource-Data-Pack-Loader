package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.ExposureDef;
import mctmods.resourcedatapackloader.content.def.ExposureLevelDef;
import mctmods.resourcedatapackloader.content.def.PotionEffectDef;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentExposures {
    private static final String[] BYPASSES = { "bypasses_armor", "bypasses_effects", "bypasses_enchantments" };
    private static final String TIMER = "RDPLExposure";
    private static final Map<ExposureDef, Map<Block, Integer>> BLOCK_LEVELS = new IdentityHashMap<>();
    private static final Map<ExposureDef, Map<Item, Integer>> ITEM_LEVELS = new IdentityHashMap<>();
    private static final Map<ExposureDef, List<MobEffect>> MARKERS = new IdentityHashMap<>();
    private static final Map<PotionEffectDef, MobEffect> EXTRAS = new IdentityHashMap<>();
    private static final Map<ExposureDef, MobEffect> IMMUNITIES = new IdentityHashMap<>();

    private ContentExposures() {}

    public static boolean enabled() { return !ContentRegistry.exposures().isEmpty(); }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) { return; }
        for (ExposureDef def : ContentRegistry.exposures()) { tick(def, player); }
    }

    private static void tick(ExposureDef def, ServerPlayer player) {
        if (def.skipsCreative() && (player.isCreative() || player.isSpectator())) {
            applyLevel(def, player, 0);
            return;
        }
        if (player.tickCount % def.scanInterval() == 0) { applyLevel(def, player, scan(def, player)); }
        damageTick(def, player);
    }

    private static int scan(ExposureDef def, ServerPlayer player) {
        if (!def.immunity().isEmpty()) {
            MobEffect immune = immunity(def);
            if (immune != null && player.hasEffect(immune)) { return 0; }
        }
        int most = def.levels().size();
        int reached = scanItems(def, player, most);
        if (reached >= most) { return most; }
        return Math.max(reached, scanWorld(def, player, most));
    }

    @Nullable private static MobEffect immunity(ExposureDef def) {
        if (IMMUNITIES.containsKey(def)) { return IMMUNITIES.get(def); }
        MobEffect found = effect(def.immunity(), def, "immunity");
        IMMUNITIES.put(def, found);
        return found;
    }

    private static int scanItems(ExposureDef def, ServerPlayer player, int most) {
        Map<Item, Integer> levels = itemLevels(def);
        if (levels.isEmpty()) { return 0; }
        int reached = scanList(levels, player.getInventory().items, 0, most);
        if (reached >= most) { return reached; }
        reached = scanList(levels, player.getInventory().offhand, reached, most);
        if (reached >= most) { return reached; }
        return scanList(levels, player.getInventory().armor, reached, most);
    }

    private static int scanList(Map<Item, Integer> levels, List<ItemStack> list, int reached, int most) {
        for (ItemStack stack : list) {
            if (stack.isEmpty()) { continue; }
            Integer found = levels.get(stack.getItem());
            if (found != null && found > reached) { reached = found; }
            if (reached >= most) { return reached; }
        }
        return reached;
    }

    private static int scanWorld(ExposureDef def, ServerPlayer player, int most) {
        Map<Block, Integer> levels = blockLevels(def);
        int radius = def.range();
        if (levels.isEmpty() || radius <= 0) { return 0; }
        ServerLevel level = player.serverLevel();
        int radiusSq = radius * radius;
        int centerX = Mth.floor(player.getX());
        int centerY = Mth.floor(player.getY());
        int centerZ = Mth.floor(player.getZ());
        int lowest = Math.max(level.getMinBuildHeight(), centerY - radius);
        int highest = Math.min(level.getMaxBuildHeight() - 1, centerY + radius);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int reached = 0;
        int sources = 0;
        for (int chunkX = centerX - radius >> 4; chunkX <= centerX + radius >> 4; chunkX++) {
            for (int chunkZ = centerZ - radius >> 4; chunkZ <= centerZ + radius >> 4; chunkZ++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
                if (chunk == null) { continue; }
                int fromX = Math.max(centerX - radius, chunkX << 4);
                int toX = Math.min(centerX + radius, (chunkX << 4) + 15);
                int fromZ = Math.max(centerZ - radius, chunkZ << 4);
                int toZ = Math.min(centerZ + radius, (chunkZ << 4) + 15);
                for (int y = lowest; y <= highest; y++) {
                    int offY = (y - centerY) * (y - centerY);
                    for (int x = fromX; x <= toX; x++) {
                        int offX = (x - centerX) * (x - centerX);
                        for (int z = fromZ; z <= toZ; z++) {
                            int offZ = (z - centerZ) * (z - centerZ);
                            if (offX + offY + offZ > radiusSq) { continue; }
                            Integer found = levels.get(chunk.getBlockState(at.set(x, y, z)).getBlock());
                            if (found == null || found <= 0) { continue; }
                            if (found > reached) { reached = found; }
                            if (reached >= most) { return most; }
                            sources++;
                        }
                    }
                }
            }
        }
        if (reached > 0 && reached < most && def.sourcesForNextLevel() > 0 && sources >= def.sourcesForNextLevel()) { return reached + 1; }
        return reached;
    }

    private static void applyLevel(ExposureDef def, ServerPlayer player, int reached) {
        int duration = def.scanInterval() * 2 + 20;
        List<MobEffect> markers = markers(def);
        for (int index = 0; index < markers.size(); index++) {
            MobEffect marker = markers.get(index);
            if (index + 1 == reached || marker == null) { continue; }
            player.removeEffect(marker);
        }
        if (reached <= 0) { return; }
        ExposureLevelDef entry = def.levels().get(reached - 1);
        MobEffect marker = markers.get(reached - 1);
        if (marker == null) { return; }
        player.addEffect(new MobEffectInstance(marker, duration, 0, false, true));
        for (PotionEffectDef extra : entry.extras()) {
            MobEffect effect = extraEffect(extra, def);
            if (effect == null) { continue; }
            player.addEffect(new MobEffectInstance(effect, extra.duration() > 0 ? extra.duration() : duration, extra.amplifier(), extra.ambient(), extra.showParticles()));
        }
    }

    private static void damageTick(ExposureDef def, ServerPlayer player) {
        ExposureLevelDef active = null;
        List<MobEffect> markers = markers(def);
        for (int index = 0; index < markers.size(); index++) {
            MobEffect marker = markers.get(index);
            if (marker != null && player.hasEffect(marker)) { active = def.levels().get(index); }
        }
        CompoundTag data = player.getPersistentData();
        String tag = TIMER + def.name();
        if (active != null && active.damage() > 0.0F && active.damageInterval() > 0) {
            int timer = data.getInt(tag) + 1;
            if (timer >= active.damageInterval()) {
                timer = 0;
                player.hurt(source(def, player), active.damage());
            }
            data.putInt(tag, timer);
        }
        else if (data.contains(tag)) { data.putInt(tag, 0); }
    }

    private static List<MobEffect> markers(ExposureDef def) {
        return MARKERS.computeIfAbsent(def, held -> {
            List<MobEffect> markers = new ArrayList<>();
            for (int index = 0; index < held.levels().size(); index++) { markers.add(effect(held.levels().get(index).effect(), held, "level " + (index + 1))); }
            return markers;
        });
    }

    @Nullable private static MobEffect extraEffect(PotionEffectDef extra, ExposureDef def) {
        if (EXTRAS.containsKey(extra)) { return EXTRAS.get(extra); }
        MobEffect found = effect(extra.potion(), def, "extra effect");
        EXTRAS.put(extra, found);
        return found;
    }

    @Nullable private static MobEffect effect(String name, ExposureDef def, String what) {
        MobEffect found = Registered.find(ForgeRegistries.MOB_EFFECTS, ResourceLocation.tryParse(name));
        if (found == null) { ContentLog.LOGGER.error("Exposure {} names effect {} as its {}, which nothing registers, so that part does nothing", def.key(), name, what); }
        return found;
    }

    private static DamageSource source(ExposureDef def, ServerPlayer player) {
        ResourceKey<DamageType> key = ResourceKey.create(Registries.DAMAGE_TYPE, def.key());
        Holder<DamageType> type = player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolder(key).orElse(null);
        return type == null ? player.damageSources().magic() : new DamageSource(type);
    }

    private static Map<Block, Integer> blockLevels(ExposureDef def) {
        return BLOCK_LEVELS.computeIfAbsent(def, held -> {
            Map<Block, Integer> levels = new IdentityHashMap<>();
            for (Map.Entry<ResourceLocation, Integer> entry : held.blocks().entrySet()) {
                Block block = Registered.find(ForgeRegistries.BLOCKS, entry.getKey());
                if (block == null) { ContentLog.LOGGER.error("Exposure {} names block {}, which is not registered, so it is ignored", held.key(), entry.getKey()); }
                else { levels.put(block, entry.getValue()); }
            }
            return levels;
        });
    }

    private static Map<Item, Integer> itemLevels(ExposureDef def) {
        return ITEM_LEVELS.computeIfAbsent(def, held -> {
            Map<Item, Integer> levels = new IdentityHashMap<>();
            for (Map.Entry<ResourceLocation, Integer> entry : held.items().entrySet()) {
                Item item = Registered.find(ForgeRegistries.ITEMS, entry.getKey());
                if (item == null) { ContentLog.LOGGER.error("Exposure {} names item {}, which is not registered, so it is ignored", held.key(), entry.getKey()); }
                else { levels.put(item, entry.getValue()); }
            }
            return levels;
        });
    }

    public static void generate() {
        if (!enabled()) { return; }
        JsonArray names = new JsonArray();
        for (ExposureDef def : ContentRegistry.exposures()) {
            JsonObject type = new JsonObject();
            type.addProperty("message_id", "rdpl." + def.name());
            type.addProperty("exhaustion", 0.0F);
            type.addProperty("scaling", "never");
            GeneratedResources.put(PackType.SERVER_DATA, def.key().getNamespace(), "damage_type/" + def.key().getPath() + ".json", type.toString());
            names.add(def.key().toString());
        }
        for (String tag : BYPASSES) {
            JsonObject values = new JsonObject();
            values.addProperty("replace", false);
            values.add("values", names);
            GeneratedResources.put(PackType.SERVER_DATA, "minecraft", "tags/damage_type/" + tag + ".json", values.toString());
        }
        Summary.info("exposures.generated", "Generated " + names.size() + " damage type(s) from exposures");
    }
}
