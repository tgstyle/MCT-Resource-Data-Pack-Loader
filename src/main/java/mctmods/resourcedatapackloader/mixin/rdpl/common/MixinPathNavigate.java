package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PathNavigate.class) public abstract class MixinPathNavigate {
    @Unique private static final double RDPL_REACH = 8.0D;

    @Shadow protected abstract boolean isDirectPathBetweenPoints(Vec3d from, Vec3d to, int sizeX, int sizeY, int sizeZ);

    @Redirect(method = "pathFollow", at = @At(value = "INVOKE", target = "Lnet/minecraft/pathfinding/PathNavigate;isDirectPathBetweenPoints(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;III)Z"))
    private boolean rdpl$onlyShortcutNearby(PathNavigate self, Vec3d from, Vec3d to, int sizeX, int sizeY, int sizeZ) {
        if (to.squareDistanceTo(from) > RDPL_REACH * RDPL_REACH) { return false; }
        return isDirectPathBetweenPoints(from, to, sizeX, sizeY, sizeZ);
    }
}
