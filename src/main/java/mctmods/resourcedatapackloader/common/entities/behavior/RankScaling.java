package mctmods.resourcedatapackloader.common.entities.behavior;

import mctmods.resourcedatapackloader.common.pack.definitions.entities.RankDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.RankScalingDef;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public final class RankScaling {
    private static final long DAY = 24000L;

    private RankScaling() {}

    public static RankScalingDef of(RankDef def) { return def.scaling == null ? RankScalingDef.NONE : def.scaling; }

    public static float steps(EntityLiving mob, @Nullable RankScalingDef scaling) {
        if (scaling == null) { return 0.0F; }
        float steps = 0.0F;
        if (scaling.difficulty > 0.0F) { steps += mob.world.getDifficulty().getId() * scaling.difficulty; }
        MinecraftServer server = mob.world.getMinecraftServer();
        if (scaling.daysPerStep > 0.0F && server != null) { steps += (float) Math.floor(server.getWorld(0).getWorldTime() / (double) DAY / scaling.daysPerStep); }
        if (scaling.distancePerStep > 0.0F) {
            BlockPos spawn = mob.world.getSpawnPoint();
            double dx = mob.posX - spawn.getX();
            double dz = mob.posZ - spawn.getZ();
            steps += (float) Math.floor(Math.sqrt(dx * dx + dz * dz) / scaling.distancePerStep);
        }
        return steps;
    }

    public static float add(float base, float perStep, float steps) { return base + steps * perStep; }

    public static float chance(float base, float perStep, float steps) { return MathHelper.clamp(add(base, perStep, steps), 0.0F, 1.0F); }
}
