package mctmods.resourcedatapackloader.common.entities.behavior;

import mctmods.resourcedatapackloader.common.pack.definitions.entities.RankDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.RankScalingDef;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

public final class RankScaling {
    private static final long DAY = 24000L;

    private RankScaling() {}

    public static RankScalingDef of(RankDef def) { return def.scaling() == null ? RankScalingDef.NONE : def.scaling(); }

    public static float steps(Mob mob, @Nullable RankScalingDef scaling) {
        if (scaling == null) { return 0.0F; }
        float steps = 0.0F;
        if (scaling.difficulty() > 0.0F) { steps += mob.level().getDifficulty().getId() * scaling.difficulty(); }
        MinecraftServer server = mob.level().getServer();
        if (scaling.daysPerStep() > 0.0F && server != null) { steps += (float) Math.floor(server.overworld().getDayTime() / (double) DAY / scaling.daysPerStep()); }
        if (scaling.distancePerStep() > 0.0F) {
            BlockPos spawn = mob.level().getSharedSpawnPos();
            double dx = mob.getX() - spawn.getX();
            double dz = mob.getZ() - spawn.getZ();
            steps += (float) Math.floor(Math.sqrt(dx * dx + dz * dz) / scaling.distancePerStep());
        }
        return steps;
    }

    public static float add(float base, float perStep, float steps) { return base + steps * perStep; }

    public static float chance(float base, float perStep, float steps) { return Mth.clamp(add(base, perStep, steps), 0.0F, 1.0F); }
}
