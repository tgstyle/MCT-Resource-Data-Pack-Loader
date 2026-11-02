package mctmods.resourcedatapackloader.common.blocks.fluid;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidBehaviorDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public final class FluidEntities {
    private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();

    private FluidEntities() {}

    public static void touch(Level level, BlockPos pos, BlockState state, Entity entity, FluidDef def) {
        if (entity instanceof LivingEntity) { creature(level, pos, state, entity, def); }
        else if (entity instanceof ItemEntity item) { item(level, pos, state, item, def); }
    }

    public static boolean lavaDefault(Entity entity, FluidBehaviorDef behavior) {
        if (entity instanceof LivingEntity) { return behavior.creatures().damage() == null && behavior.creatures().fireSeconds() == null; }
        if (entity instanceof ItemEntity) { return unkeyed(behavior.items()); }
        return true;
    }

    private static boolean unkeyed(FluidBehaviorDef.Items keys) { return keys.burn() == null && keys.immune().isEmpty(); }

    private static void creature(Level level, BlockPos pos, BlockState state, Entity entity, FluidDef def) {
        FluidBehaviorDef.Creatures keys = def.behavior().creatures();
        if (keys == FluidBehaviorDef.Creatures.NONE) { return; }
        Float drag = keys.drag();
        if (drag != null && pos.equals(entity.blockPosition())) { entity.setDeltaMovement(entity.getDeltaMovement().scale(drag)); }
        if (level.isClientSide() || dry(level, pos, state, entity, def)) { return; }
        Integer fire = keys.fireSeconds();
        if (fire != null && fire > 0) { entity.setSecondsOnFire(fire); }
        Float damage = keys.damage();
        if (damage != null && damage > 0.0F && entity.tickCount % keys.damageEvery() == 0) { entity.hurt(source(level, keys.damageType()), damage); }
    }

    private static void item(Level level, BlockPos pos, BlockState state, ItemEntity entity, FluidDef def) {
        FluidBehaviorDef.Items keys = def.behavior().items();
        if (keys == FluidBehaviorDef.Items.NONE) { return; }
        boolean feet = pos.equals(entity.blockPosition());
        Float drift = keys.drift();
        if (feet && drift != null) { flow(level, pos, state, entity, def, drift); }
        Float buoyancy = keys.buoyancy();
        if (feet && buoyancy != null) {
            Vec3 motion = entity.getDeltaMovement().add(0.0D, buoyancy, 0.0D);
            entity.setDeltaMovement(motion);
            if (motion.y > 0.0D) { entity.setOnGround(false); }
        }
        if (level.isClientSide() || dry(level, pos, state, entity, def) || immune(entity, keys)) { return; }
        Boolean asked = keys.burn();
        boolean burn = asked != null ? asked : def.lavaMaterial();
        if (!unkeyed(keys) && burn) { entity.lavaHurt(); }
        if (feet && keys.dissolve() > 0.0F && level.random.nextFloat() < keys.dissolve()) { entity.discard(); }
    }

    private static boolean dry(Level level, BlockPos pos, BlockState state, Entity entity, FluidDef def) {
        double filled = state.getFluidState().getHeight(level, pos);
        AABB box = entity.getBoundingBox();
        return def.gaseous() ? box.maxY <= pos.getY() + 1.0D - filled : box.minY >= pos.getY() + filled;
    }

    private static void flow(Level level, BlockPos pos, BlockState state, Entity entity, FluidDef def, float scale) {
        FluidState fluid = state.getFluidState();
        if (def.gaseous() || !entity.isPushedByFluid()) { return; }
        entity.setDeltaMovement(entity.getDeltaMovement().add(fluid.getFlow(level, pos).normalize().scale(scale)));
    }

    private static boolean immune(ItemEntity entity, FluidBehaviorDef.Items keys) {
        if (keys.immune().isEmpty() || entity.getItem().isEmpty()) { return false; }
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(entity.getItem().getItem());
        return id != null && keys.immune().contains(id.toString());
    }

    private static DamageSource source(Level level, String name) {
        ResourceLocation id = ResourceLocation.tryParse(name.toLowerCase(Locale.ROOT));
        Holder<DamageType> held = id == null ? null : damageType(level, id);
        if (held != null) { return new DamageSource(held); }
        if (WARNED.add(name)) { ContentLog.LOGGER.error("A fluid names the damage type '{}', which is not registered, so generic damage is used instead", name); }
        return level.damageSources().generic();
    }

    @Nullable private static Holder<DamageType> damageType(Level level, ResourceLocation id) {
        return level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolder(ResourceKey.create(Registries.DAMAGE_TYPE, id)).orElse(null);
    }
}
