package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.compat.LineCompat;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonElement;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentLootSwitch extends LineCompat.Modifier {
    private static final Identifier EMPTY = Identifier.withDefaultNamespace("empty");
    public static final MapCodec<ContentLootSwitch> CODEC = RecordCodecBuilder.mapCodec(instance -> LineCompat.Modifier.start(instance).apply(instance, ContentLootSwitch::new));
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> REGISTER = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ResourceDataPackLoader.MOD_ID);

    static { REGISTER.register("environment_loot", () -> CODEC); }

    private ContentLootSwitch(LineCompat.ModifierStart start) { super(start); }

    @Override @Nonnull public MapCodec<? extends IGlobalLootModifier> codec() { return CODEC; }

    @Nullable static Map<Identifier, Identifier> parse(String owner, JsonElement element) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives loot that is not an object of loot table to loot table, ignoring it", owner);
            return null;
        }
        Map<Identifier, Identifier> swaps = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            Identifier from = id(entry.getKey());
            if (from == null) {
                ContentLog.LOGGER.error("{} gives loot for '{}', which is not a loot table id, ignoring it", owner, entry.getKey());
                continue;
            }
            if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isString()) {
                ContentLog.LOGGER.error("{} gives loot.{} that is not a loot table id, ignoring it", owner, entry.getKey());
                continue;
            }
            Identifier to = id(entry.getValue().getAsString());
            if (to == null) {
                ContentLog.LOGGER.error("{} gives loot.{} as '{}', which is not a loot table id, ignoring it", owner, entry.getKey(), entry.getValue().getAsString());
                continue;
            }
            if (from.equals(to)) {
                ContentLog.LOGGER.error("{} swaps the loot table '{}' for itself, ignoring it", owner, from);
                continue;
            }
            swaps.put(from, to);
        }
        return swaps;
    }

    @Nullable private static Identifier id(String text) {
        String lowered = Settings.lower(text);
        return lowered.isEmpty() ? null : Identifier.tryParse(lowered);
    }

    @SuppressWarnings("deprecation") @Override @Nonnull protected ObjectArrayList<ItemStack> doApply(@Nonnull ObjectArrayList<ItemStack> generatedLoot, @Nonnull LootContext context) {
        Vec3 origin = LineCompat.param(context, LootContextParams.ORIGIN);
        if (origin == null && LineCompat.param(context, LootContextParams.THIS_ENTITY) instanceof Entity entity) { origin = entity.position(); }
        if (origin == null) { return generatedLoot; }
        ServerLevel level = context.getLevel();
        Identifier to = ContentEnvironments.loot(level, BlockPos.containing(origin)).get(context.getQueriedLootTableId());
        if (to == null) { return generatedLoot; }
        ObjectArrayList<ItemStack> swapped = new ObjectArrayList<>();
        table(level.getServer(), to).getRandomItemsRaw(context, LootTable.createStackSplitter(level, swapped::add));
        return swapped;
    }

    private static LootTable table(MinecraftServer server, Identifier id) { return server.reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, id)); }

    static void start(MinecraftServer server, Collection<Map<Identifier, Identifier>> maps) {
        Set<Identifier> checked = new LinkedHashSet<>();
        for (Map<Identifier, Identifier> swaps : maps) {
            for (Map.Entry<Identifier, Identifier> entry : swaps.entrySet()) {
                missing(server, checked, entry.getKey());
                missing(server, checked, entry.getValue());
            }
        }
    }

    private static void missing(MinecraftServer server, Set<Identifier> checked, Identifier id) {
        if (EMPTY.equals(id) || !checked.add(id)) { return; }
        if (table(server, id) == LootTable.EMPTY) { ContentLog.LOGGER.error("{} names no loot table, so an environment loot swap that gives it has no effect", id); }
    }
}
