package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.util.ContentAttributes;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IEntityType;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ContentEntityTypes {
    private static final ResourceLocation RETURNING_THROW = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "returning_throw");
    private static final String SPAWN_EGG_MODEL = "{\"parent\":\"minecraft:item/template_spawn_egg\"}";

    private ContentEntityTypes() {}

    public static void registerTypes(RegisterEvent.RegisterHelper<EntityType<?>> helper) {
        int made = 0;
        for (EntityVariantDef def : ContentEntities.DEFS.values()) {
            EntityType<?> base = Registered.find(BuiltInRegistries.ENTITY_TYPE, def.base());
            if (base == null) {
                ContentLog.LOGGER.error("Entity variant {} is based on {}, which nothing registers, leaving it out", def.key(), def.base());
                continue;
            }
            EntityType.EntityFactory<?> baseFactory = ((IEntityType) base).rdpl$factory();
            EntityType.EntityFactory<Entity> factory = (type, level) -> EntityClassMaker.make(baseFactory, type, level, def);
            EntityDimensions dims = base.getDimensions();
            float width = def.width() > 0.0F ? def.width() : dims.width();
            float height = def.height() > 0.0F ? def.height() : dims.height();
            EntityType.Builder<Entity> builder = EntityType.Builder.of(factory, def.hostile() ? MobCategory.MONSTER : base.getCategory())
                    .sized(width, height)
                    .clientTrackingRange(def.tracking().range()).updateInterval(def.tracking().frequency()).setShouldReceiveVelocityUpdates(def.tracking().velocity());
            if (def.flags().fireproof()) { builder = builder.fireImmune(); }
            EntityType<Entity> type = builder.build(def.key().toString());
            helper.register(def.key(), type);
            ContentEntities.BY_TYPE.put(type, def);
            ContentEntities.BASES.put(type, base);
            ContentEntities.TYPES.put(def.key(), type);
            made++;
            ContentLog.LOGGER.debug("Entity variant {} read from the pack with attributes {} and equipment {}", def.key(), def.attributes(), def.equipment());
        }
        if (throwsReturning()) {
            ContentEntities.returningThrow = EntityType.Builder.<ReturningThrow>of(ReturningThrow::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(1).setShouldReceiveVelocityUpdates(true).build(RETURNING_THROW.toString());
            helper.register(RETURNING_THROW, ContentEntities.returningThrow);
        }
        if (made > 0) { Summary.info("entities.registered", "Registered " + made + " entity variant(s) from packs"); }
    }

    private static boolean throwsReturning() {
        for (EntityVariantDef def : ContentEntities.DEFS.values()) {
            if (def.combat().throwsItems() && def.combat().throwReturns()) { return true; }
        }
        return false;
    }

    public static void registerEggs(RegisterEvent.RegisterHelper<Item> helper) {
        for (EntityVariantDef def : ContentEntities.DEFS.values()) {
            if (!def.egg().wanted()) { continue; }
            EntityType<?> base = Registered.find(BuiltInRegistries.ENTITY_TYPE, def.base());
            if (base == null) { continue; }
            SpawnEggItem baseEgg = SpawnEggItem.byId(base);
            int primary = def.egg().primary() >= 0 ? def.egg().primary() : baseEgg == null ? 0xFFFFFF : baseEgg.getColor(0);
            int secondary = def.egg().secondary() >= 0 ? def.egg().secondary() : baseEgg == null ? 0x808080 : baseEgg.getColor(1);
            ResourceLocation key = def.key();
            helper.register(ResourceLocation.fromNamespaceAndPath(key.getNamespace(), key.getPath() + "_spawn_egg"), new DeferredSpawnEggItem(() -> egged(ContentEntities.TYPES.get(key)), primary, secondary, new Item.Properties()));
        }
    }

    @SuppressWarnings("unchecked") private static EntityType<? extends Mob> egged(EntityType<?> type) { return (EntityType<? extends Mob>) type; }

    @SuppressWarnings("unchecked") private static EntityType<? extends LivingEntity> living(EntityType<?> type) { return (EntityType<? extends LivingEntity>) type; }

    private static boolean alive(EntityType<?> type) {
        EntityType<?> base = ContentEntities.BASES.get(type);
        return base != null && DefaultAttributes.hasSupplier(base);
    }

    public static void attributes(EntityAttributeCreationEvent event) {
        for (EntityType<?> type : ContentEntities.TYPES.values()) {
            if (alive(type)) { event.put(living(type), DefaultAttributes.getSupplier(living(ContentEntities.BASES.get(type)))); }
        }
    }

    public static void extraAttributes(EntityAttributeModificationEvent event) {
        for (EntityType<?> type : ContentEntities.TYPES.values()) {
            if (!alive(type)) { continue; }
            EntityVariantDef def = ContentEntities.BY_TYPE.get(type);
            for (String name : def.attributes().keySet()) {
                Holder<Attribute> attribute = ContentAttributes.find(name, def.key());
                if (attribute != null && !event.has(living(type), attribute)) { event.add(living(type), attribute); }
            }
        }
    }

    public static void placements(RegisterSpawnPlacementsEvent event) {
        for (EntityType<?> type : ContentEntities.TYPES.values()) { place(event, type, ContentEntities.BASES.get(type), ContentEntities.BY_TYPE.get(type).flags().ignoresSpawnRules()); }
    }

    private static <T extends Entity> void place(RegisterSpawnPlacementsEvent event, EntityType<T> type, EntityType<?> base, boolean free) { event.register(type, SpawnPlacements.getPlacementType(base), SpawnPlacements.getHeightmapType(base), (kind, level, reason, pos, random) -> free || rules(base, level, reason, pos, random), RegisterSpawnPlacementsEvent.Operation.REPLACE); }

    private static <T extends Entity> boolean rules(EntityType<T> base, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) { return SpawnPlacements.checkSpawnRules(base, level, reason, pos, random); }

    private static void creatureTags(EntityVariantDef def) {
        List<String> tags = switch (def.creatureAttribute()) {
            case "" , "undefined" -> List.of();
            case "undead" -> List.of("minecraft:undead", "minecraft:sensitive_to_smite", "minecraft:ignores_poison_and_regen", "minecraft:inverted_healing_and_harm", "minecraft:wither_friends");
            case "arthropod" -> List.of("minecraft:arthropod", "minecraft:sensitive_to_bane_of_arthropods");
            case "illager" -> List.of("minecraft:illager", "minecraft:illager_friends");
            case "water" -> List.of("minecraft:aquatic", "minecraft:sensitive_to_impaling", "minecraft:can_breathe_under_water");
            default -> {
                ContentLog.LOGGER.error("Entity variant {} names creature attribute '{}', which is not one of undefined, undead, arthropod, illager or water", def.key(), def.creatureAttribute());
                yield List.of();
            }
        };
        for (String tag : tags) { TAGS.computeIfAbsent(tag, k -> new java.util.LinkedHashSet<>()).add(def.key().toString()); }
    }

    private static final Map<String, java.util.Set<String>> TAGS = new LinkedHashMap<>();

    private static void writeTags() {
        for (Map.Entry<String, java.util.Set<String>> entry : TAGS.entrySet()) {
            ResourceLocation tag = ResourceLocation.tryParse(entry.getKey());
            if (tag == null) { continue; }
            JsonObject json = new JsonObject();
            json.addProperty("replace", false);
            JsonArray values = new JsonArray();
            for (String id : entry.getValue()) { values.add(id); }
            json.add("values", values);
            GeneratedResources.put(PackType.SERVER_DATA, tag.getNamespace(), ContentFormats.ENTITY_TYPE_TAGS + "/" + tag.getPath() + ".json", json.toString());
        }
        TAGS.clear();
    }

    public static void generate() {
        if (Config.contentOff()) { return; }
        int eggs = 0;
        for (EntityVariantDef def : ContentEntities.DEFS.values()) {
            creatureTags(def);
            if (eggModel(def)) { eggs++; }
        }
        writeTags();
        int spawning = ContentEntitySpawns.generate(ContentEntities.DEFS.values());
        if (spawning > 0) { Summary.info("entities.spawns", "Added the natural spawns of " + spawning + " entity variant(s) to their biomes"); }
        if (eggs > 0) { Summary.info("entities.eggs", "Generated " + eggs + " spawn egg model(s) that the packs did not ship themselves"); }
    }

    private static boolean eggModel(EntityVariantDef def) {
        if (!def.egg().wanted()) { return false; }
        String namespace = def.key().getNamespace();
        String path = "models/item/" + def.key().getPath() + "_spawn_egg.json";
        if (PackManager.get().provides(PackType.CLIENT_RESOURCES, namespace, path)) { return false; }
        GeneratedResources.put(PackType.CLIENT_RESOURCES, namespace, path, SPAWN_EGG_MODEL);
        return true;
    }
}
