package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentBells;
import mctmods.resourcedatapackloader.content.ContentRaids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentBellBlockEntity extends BlockEntity {
    private static final int SHAKE_TICKS = 50;
    private static final int RESONATE_AFTER = 5;
    private static final int RESONATE_TICKS = 40;
    private static final int SEARCH_EVERY = 60;
    private static final int SEARCH_RADIUS = 48;
    private static final int HEAR_RADIUS = 32;
    private static final int RESONATE_RADIUS = 32;
    private static final int GLOW_RADIUS = 48;
    private static final int PARTICLE_COLOR = 16700985;
    private static final int PARTICLE_STEP = 5;
    private static final int CROWD_OFFSET = 21;
    private static final int LEAST_PARTICLES = 3;
    private static final int MOST_PARTICLES = 15;
    private long lastSearch;
    private int ticks;
    private boolean shaking;
    private Direction clicked = Direction.NORTH;
    @Nullable private List<LivingEntity> nearby;
    private boolean resonating;
    private int resonateTicks;

    public ContentBellBlockEntity(BlockPos pos, BlockState state) { super(ContentBells.type(), pos, state); }

    public int ticks() { return ticks; }

    public boolean shaking() { return shaking; }

    public Direction clicked() { return clicked; }

    public void hit(Direction side) {
        clicked = side;
        if (shaking) { ticks = 0; }
        else { shaking = true; }
        if (level != null) { level.blockEvent(worldPosition, getBlockState().getBlock(), ContentBells.RING_EVENT, side.get3DDataValue()); }
    }

    @Override public boolean triggerEvent(int id, int value) {
        if (id != ContentBells.RING_EVENT) { return super.triggerEvent(id, value); }
        listen();
        resonateTicks = 0;
        clicked = Direction.from3DDataValue(value);
        ticks = 0;
        shaking = true;
        return true;
    }

    private void listen() {
        if (level == null || level.isClientSide()) { return; }
        long now = level.getGameTime();
        if (now > lastSearch + SEARCH_EVERY || nearby == null) {
            lastSearch = now;
            nearby = level.getEntitiesOfClass(LivingEntity.class, new AABB(worldPosition).inflate(SEARCH_RADIUS));
        }
        for (LivingEntity living : nearby) {
            if (living instanceof Villager villager && within(living, HEAR_RADIUS)) { ContentRaids.hear(villager, now); }
        }
    }

    private boolean within(LivingEntity living, int radius) {
        return living.isAlive() && worldPosition.distToCenterSqr(living.position()) < (double) radius * radius;
    }

    private boolean raiderWithin(LivingEntity living, int radius) { return within(living, radius) && ContentRaids.answersBell(living); }

    public static void tick(Level level, BlockPos ignoredPos, BlockState state, ContentBellBlockEntity held) { held.tick(level, state); }

    private void tick(Level level, BlockState state) {
        if (shaking) { ticks++; }
        if (ticks >= SHAKE_TICKS) {
            shaking = false;
            ticks = 0;
        }
        if (level.isClientSide() || nearby == null) { return; }
        if (ticks >= RESONATE_AFTER && resonateTicks == 0 && raidersNear(nearby)) {
            resonating = true;
            if (state.getBlock() instanceof ContentBellBlock bell) { bell.resonate(level, worldPosition); }
        }
        if (!resonating) { return; }
        if (resonateTicks < RESONATE_TICKS) { resonateTicks++; }
        else {
            reveal((ServerLevel) level, nearby);
            resonating = false;
        }
    }

    private boolean raidersNear(List<LivingEntity> around) {
        for (LivingEntity living : around) {
            if (raiderWithin(living, RESONATE_RADIUS)) { return true; }
        }
        return false;
    }

    private void reveal(ServerLevel level, List<LivingEntity> around) {
        int heard = 0;
        for (LivingEntity living : around) {
            if (worldPosition.distToCenterSqr(living.position()) < (double) GLOW_RADIUS * GLOW_RADIUS) { heard++; }
        }
        int per = Mth.clamp((heard - CROWD_OFFSET) / -2, LEAST_PARTICLES, MOST_PARTICLES);
        int color = PARTICLE_COLOR;
        for (LivingEntity living : around) {
            if (!raiderWithin(living, GLOW_RADIUS)) { continue; }
            ContentRaids.glow(living);
            double dx = living.getX() - worldPosition.getX();
            double dz = living.getZ() - worldPosition.getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);
            double x = worldPosition.getX() + 0.5D + dx / distance;
            double z = worldPosition.getZ() + 0.5D + dz / distance;
            for (int i = 0; i < per; i++) {
                color += PARTICLE_STEP;
                level.sendParticles(ParticleTypes.ENTITY_EFFECT, x, worldPosition.getY() + 0.5D, z, 0, (color >> 16 & 255) / 255.0D, (color >> 8 & 255) / 255.0D, (color & 255) / 255.0D, 1.0D);
            }
        }
    }

    @Override @Nonnull public AABB getRenderBoundingBox() { return new AABB(worldPosition); }
}
