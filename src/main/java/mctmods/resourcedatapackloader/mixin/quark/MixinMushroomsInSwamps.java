package mctmods.resourcedatapackloader.mixin.quark;

import mctmods.resourcedatapackloader.content.worldgen.ContentCascade;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.quark.world.feature.MushroomsInSwamps;

@Mixin(value = MushroomsInSwamps.class, remap = false)
public abstract class MixinMushroomsInSwamps {
    @Unique private static final int MUSHROOM_REACH = 3;

    @Redirect(method = "decorate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getHeight(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/BlockPos;", remap = true))
    private BlockPos rdpl$onlyPlaceLoaded(World world, BlockPos pos) {
        if (!ContentCascade.loaded(world, pos, MUSHROOM_REACH)) { return new BlockPos(pos.getX(), 0, pos.getZ()); }

        return world.getHeight(pos);
    }
}
