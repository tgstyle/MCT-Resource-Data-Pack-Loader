package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.def.StructureMapDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentLootSwitch extends LootModifier {
    private static final ResourceLocation EMPTY = ResourceLocation.parse("minecraft:empty");
    public static final Codec<ContentLootSwitch> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance).apply(instance, ContentLootSwitch::new));
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> REGISTER = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ResourceDataPackLoader.MOD_ID);

    static { REGISTER.register("environment_loot", () -> CODEC); }

    public ContentLootSwitch(LootItemCondition[] conditions) { super(conditions); }

    @Override @Nonnull public Codec<? extends IGlobalLootModifier> codec() { return CODEC; }

    @Nullable static Map<ResourceLocation, ResourceLocation> parse(String owner, @Nullable JsonElement element) {
        if (element == null) { return null; }
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives loot that is not an object of loot table ids, ignoring it", owner);
            return null;
        }
        Map<ResourceLocation, ResourceLocation> swaps = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            ResourceLocation from = id(entry.getKey());
            JsonElement value = entry.getValue();
            ResourceLocation to = value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() ? id(value.getAsString()) : null;
            if (from == null || to == null) { ContentLog.LOGGER.error("{} gives loot for '{}' as {}, which is not a pair of loot table ids, ignoring it", owner, entry.getKey(), value); }
            else if (from.equals(to)) { ContentLog.LOGGER.error("{} swaps loot table {} for itself, ignoring it", owner, from); }
            else { swaps.put(from, to); }
        }
        return swaps;
    }

    @Nullable static ResourceLocation id(String text) {
        String lowered = Settings.lower(text);
        return lowered.isEmpty() ? null : ResourceLocation.tryParse(lowered.indexOf(':') < 0 ? "minecraft:" + lowered : lowered);
    }

    static void start(MinecraftServer server, Collection<Map<ResourceLocation, ResourceLocation>> maps) {
        Set<ResourceLocation> missing = new HashSet<>();
        for (Map<ResourceLocation, ResourceLocation> swaps : maps) {
            for (Map.Entry<ResourceLocation, ResourceLocation> swap : swaps.entrySet()) {
                check(server, swap.getKey(), missing);
                check(server, swap.getValue(), missing);
            }
        }
    }

    static void startMaps(MinecraftServer server, Collection<StructureMapDef> maps) {
        Set<ResourceLocation> missing = new HashSet<>();
        for (StructureMapDef def : maps) {
            if (def.loot() != null && absent(server, def.loot(), missing)) { ContentLog.LOGGER.error("Structure map {} gives loot {}, which names no loot table, so the chests it fills drop nothing", def.key(), def.loot()); }
        }
    }

    private static boolean absent(MinecraftServer server, ResourceLocation id, Set<ResourceLocation> missing) {
        return !EMPTY.equals(id) && server.getLootData().getLootTable(id) == LootTable.EMPTY && missing.add(id);
    }

    private static void check(MinecraftServer server, ResourceLocation id, Set<ResourceLocation> missing) {
        if (absent(server, id, missing)) { ContentLog.LOGGER.error("Environment loot {} names no loot table, so swapping it does nothing", id); }
    }

    @SuppressWarnings("deprecation") @Override @Nonnull protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (origin == null && entity != null) { origin = entity.position(); }
        if (origin == null) { return generatedLoot; }
        ResourceLocation to = ContentEnvironments.loot(context.getLevel(), BlockPos.containing(origin)).get(context.getQueriedLootTableId());
        if (to == null) { return generatedLoot; }
        ObjectArrayList<ItemStack> out = new ObjectArrayList<>();
        context.getLevel().getServer().getLootData().getLootTable(to).getRandomItemsRaw(context, LootTable.createStackSplitter(context.getLevel(), out::add));
        return out;
    }
}
