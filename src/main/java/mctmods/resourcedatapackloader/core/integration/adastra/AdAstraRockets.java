package mctmods.resourcedatapackloader.core.integration.adastra;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.FilterDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.EntityVariantDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.RocketDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.AdAstraDef;

import com.google.common.collect.MapMaker;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public final class AdAstraRockets {
    public static final long BASE_TANK = 3000L;
    private static final int CARGO_FIRST = 2;
    private static final int WARNING_COOLDOWN = 250;
    private static final double PAD_REACH = 0.5D;
    private static final String PLACED = "rdplPlaced";
    private static final String PICKED = "rdplPicked";
    private static final String PAYLOAD_MISSING = "rdpl.rocket.payloadMissing";
    private static final Map<EntityType<?>, Rocket.RocketProperties> PROPERTIES = new ConcurrentHashMap<>();
    private static final Map<Entity, Long> WARNED = new MapMaker().weakKeys().makeMap();

    private AdAstraRockets() {}

    @Nullable public static RocketDef def(EntityType<?> type) {
        EntityVariantDef def = ContentEntities.def(type);
        return def == null ? null : def.rocket();
    }

    @Nullable public static RocketDef def(Entity rocket) { return def(rocket.getType()); }

    @Nullable public static RocketDef def(Item item) {
        EntityType<?> type = item instanceof PackRocketItem pack ? pack.type() : null;
        return type == null ? null : def(type);
    }

    public static boolean rocket(EntityType<?> base) { return Rocket.ROCKET_TO_PROPERTIES.containsKey(base); }

    public static Object properties(Map<?, ?> base, Object type) {
        Object found = base.get(type);
        if (found != null || !(type instanceof EntityType<?> variant)) { return found; }
        return PROPERTIES.computeIfAbsent(variant, AdAstraRockets::built);
    }

    public static long tank(Entity rocket, long base) { return tank(def(rocket), base); }

    public static long tank(Item item) { return tank(def(item), BASE_TANK); }

    public static long burn(Level level, long millibuckets) {
        AdAstraDef body = AdAstraHeights.body(level);
        return body == null || body.fuelMultiplier() <= 0.0D ? millibuckets : Math.round(millibuckets * body.fuelMultiplier());
    }

    public static int room(RocketDef def, Container rocket, ItemStack stack) { return def.cargo().room(stack, cargo(rocket)); }

    public static boolean grounded(Rocket rocket) {
        RocketDef def = def(rocket);
        if (def == null || rocket.level().isClientSide()) { return false; }
        List<ItemStack> cargo = cargo(rocket.inventory());
        for (FilterDef.Entry entry : def.requiredPayload()) {
            if (entry.held(cargo) >= entry.amount()) { continue; }
            long now = rocket.level().getGameTime();
            Long warned = WARNED.get(rocket);
            if (warned != null && now - warned < WARNING_COOLDOWN) { return true; }
            WARNED.put(rocket, now);
            Component wanted = wanted(entry);
            for (Entity rider : rocket.getPassengers()) {
                if (rider instanceof ServerPlayer player) { player.sendSystemMessage(Component.translatable(PAYLOAD_MISSING, entry.amount(), wanted)); }
            }
            return true;
        }
        return false;
    }

    @Nullable public static Rocket placed(Level level, BlockPos pad, EntityType<?> type, boolean picked) {
        if (level.isClientSide()) { return null; }
        for (Rocket rocket : level.getEntitiesOfClass(Rocket.class, new AABB(pad).inflate(PAD_REACH), found -> found.getType() == type && found.tickCount == 0 && !found.getPersistentData().getBoolean(PLACED))) {
            rocket.getPersistentData().putBoolean(PLACED, true);
            RocketDef def = def(rocket);
            if (def != null && !picked) { preload(def, rocket.inventory()); }
            return rocket;
        }
        return null;
    }

    public static boolean picked(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(PICKED);
    }

    public static void dropped(Entity rocket, ItemStack stack) { if (def(rocket) != null) { stack.getOrCreateTag().putBoolean(PICKED, true); } }

    private static long tank(@Nullable RocketDef def, long base) { return def == null || def.fuelTank() < 0 ? base : def.fuelTank(); }

    @Nullable private static Rocket.RocketProperties built(EntityType<?> variant) {
        EntityVariantDef def = ContentEntities.def(variant);
        EntityType<?> base = ContentEntities.base(variant);
        Rocket.RocketProperties from = base == null ? null : Rocket.ROCKET_TO_PROPERTIES.get(base);
        if (def == null || def.rocket() == null || from == null) { return from; }
        Item item = PackRocketItem.placing(def.key());
        return new Rocket.RocketProperties(def.rocket().tier() < 0 ? from.tier() : def.rocket().tier(), item == null ? from.item() : item, from.ridingOffset(), from.fuel());
    }

    private static Component wanted(FilterDef.Entry entry) {
        Item item = entry.item();
        if (entry.tag() != null) { return Component.literal("#" + entry.tag().location()); }
        return item == null ? Component.literal(entry.named()) : item.getDescription();
    }

    private static void preload(RocketDef def, Container cargo) {
        int slot = CARGO_FIRST;
        for (FilterDef.Entry entry : def.payload()) {
            Item item = entry.item();
            if (item == null) { continue; }
            ItemStack wanted = new ItemStack(item);
            for (int left = entry.amount(); left > 0 && slot < cargo.getContainerSize(); slot++) {
                ItemStack loaded = wanted.copyWithCount(Math.min(left, wanted.getMaxStackSize()));
                left -= loaded.getCount();
                cargo.setItem(slot, loaded);
            }
        }
    }

    private static List<ItemStack> cargo(Container rocket) {
        List<ItemStack> held = new ArrayList<>();
        for (int slot = CARGO_FIRST; slot < rocket.getContainerSize(); slot++) { held.add(rocket.getItem(slot)); }
        return held;
    }
}
