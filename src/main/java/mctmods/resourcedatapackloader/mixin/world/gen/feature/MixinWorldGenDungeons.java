package mctmods.resourcedatapackloader.mixin.world.gen.feature;

import mctmods.resourcedatapackloader.common.worldgen.placement.ContentStructurePlacement;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.rail.BeardRails;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Random;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldGenDungeons.class) public abstract class MixinWorldGenDungeons {
    @Unique private static final int rdpl$REACH = 4;
    @Unique private static final int rdpl$UNDER = 1;
    @Unique private static final int rdpl$OVER = 4;

    @Inject(method = "generate", at = @At("HEAD"), cancellable = true) private void rdpl$notIntoABore(World worldIn, Random rand, BlockPos position, CallbackInfoReturnable<Boolean> cir) {
        if (BeardRails.clearOfBore(worldIn, position.getX() - rdpl$REACH, position.getY() - rdpl$UNDER, position.getZ() - rdpl$REACH, position.getX() + rdpl$REACH, position.getY() + rdpl$OVER, position.getZ() + rdpl$REACH)) { return; }
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("A dungeon at {}, {}, {} would have opened into a subway bore, so it is left out", position.getX(), position.getY(), position.getZ()); }
        cir.setReturnValue(false);
    }

    @Redirect(method = "generate", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/MobSpawnerBaseLogic;setEntityId(Lnet/minecraft/util/ResourceLocation;)V")) private void rdpl$spawner(MobSpawnerBaseLogic logic, ResourceLocation id, World worldIn, Random rand, BlockPos position) { logic.setEntityId(ContentStructurePlacement.spawner(ContentStructurePlacement.DUNGEONS, id, rand)); }
}
