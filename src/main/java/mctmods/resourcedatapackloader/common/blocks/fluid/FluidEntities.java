package mctmods.resourcedatapackloader.common.blocks.fluid;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidBehaviorDef;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class FluidEntities {
    private static final float LAVA_DAMAGE = 4.0F;
    private static final int LAVA_FIRE_SECONDS = 15;
    private static final Map<String, DamageSource> SOURCES = new ConcurrentHashMap<>();

    private FluidEntities() {}

    public static void touch(World world, BlockPos pos, Entity entity, ContentFluidBlock block) {
        if (entity instanceof EntityLivingBase) { creature(world, pos, entity, block); }
        else if (entity instanceof EntityItem) { item(world, pos, (EntityItem) entity, block); }
    }

    private static void creature(World world, BlockPos pos, Entity entity, ContentFluidBlock block) {
        FluidBehaviorDef.Creatures keys = block.behavior().creatures;
        boolean lavaDefault = block.lavaBurnOff() && keys.damage == null && keys.fireSeconds == null;
        if (keys == FluidBehaviorDef.Creatures.NONE && !lavaDefault) { return; }
        if (pos.equals(entity.getPosition())) {
            if (keys.push != null) { flow(world, pos, entity, block, keys.push); }
            if (keys.drag != null) { drag(entity, keys.drag); }
        }
        if (world.isRemote || dry(pos, entity, block)) { return; }
        if (lavaDefault && !entity.isImmuneToFire()) {
            entity.attackEntityFrom(DamageSource.LAVA, LAVA_DAMAGE);
            entity.setFire(LAVA_FIRE_SECONDS);
        }
        if (keys.fireSeconds != null && keys.fireSeconds > 0) { entity.setFire(keys.fireSeconds); }
        if (keys.damage != null && keys.damage > 0.0F && entity.ticksExisted % keys.damageEvery == 0) { entity.attackEntityFrom(source(keys.damageType), keys.damage); }
    }

    private static void item(World world, BlockPos pos, EntityItem entity, ContentFluidBlock block) {
        FluidBehaviorDef.Items keys = block.behavior().items;
        if (keys == FluidBehaviorDef.Items.NONE && !block.lavaBurnOff()) { return; }
        boolean feet = pos.equals(entity.getPosition());
        if (feet && keys.drift != null) { flow(world, pos, entity, block, keys.drift); }
        if (feet && keys.buoyancy != null) {
            entity.motionY += keys.buoyancy;
            if (entity.motionY > 0.0D) { entity.onGround = false; }
        }
        if (world.isRemote || dry(pos, entity, block) || immune(entity, keys.immune)) { return; }
        boolean burn = keys.burn != null ? keys.burn : block.getDefaultState().getMaterial() == Material.LAVA;
        if (burn && (block.lavaBurnOff() || block.getDefaultState().getMaterial() != Material.LAVA) && !entity.isImmuneToFire()) {
            entity.attackEntityFrom(DamageSource.LAVA, LAVA_DAMAGE);
            entity.setFire(LAVA_FIRE_SECONDS);
        }
        if (feet && keys.dissolve > 0.0F && world.rand.nextFloat() < keys.dissolve) { entity.setDead(); }
    }

    private static boolean dry(BlockPos pos, Entity entity, ContentFluidBlock block) {
        double filled = block.getFilledPercentage(entity.world, pos);
        return filled < 0.0D ? entity.getEntityBoundingBox().maxY <= pos.getY() + 1.0D + filled : entity.getEntityBoundingBox().minY >= pos.getY() + filled;
    }

    private static void flow(World world, BlockPos pos, Entity entity, ContentFluidBlock block, float scale) {
        if (block.getFluid().isLighterThanAir() || !entity.isPushedByWater()) { return; }
        Vec3d vector = block.getFlowVector(world, pos);
        if (vector.lengthSquared() == 0.0D) { return; }
        Vec3d direction = vector.normalize();
        entity.motionX += direction.x * scale;
        entity.motionY += direction.y * scale;
        entity.motionZ += direction.z * scale;
    }

    private static void drag(Entity entity, float factor) {
        entity.motionX *= factor;
        entity.motionY *= factor;
        entity.motionZ *= factor;
    }

    private static boolean immune(EntityItem entity, List<String> immune) {
        if (immune.isEmpty() || entity.getItem().isEmpty() || entity.getItem().getItem().getRegistryName() == null) { return false; }
        return immune.contains(entity.getItem().getItem().getRegistryName().toString());
    }

    private static DamageSource source(String name) {
        switch (name.toLowerCase(Locale.ROOT)) {
            case "lava": return DamageSource.LAVA;
            case "in_fire": return DamageSource.IN_FIRE;
            case "on_fire": return DamageSource.ON_FIRE;
            case "drown": return DamageSource.DROWN;
            case "magic": return DamageSource.MAGIC;
            case "wither": return DamageSource.WITHER;
            case "hot_floor": return DamageSource.HOT_FLOOR;
            case "generic": return DamageSource.GENERIC;
            default: return SOURCES.computeIfAbsent(name, DamageSource::new);
        }
    }
}
