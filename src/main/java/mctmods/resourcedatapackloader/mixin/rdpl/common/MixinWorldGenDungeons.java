package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRails;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WorldGenDungeons.class) public abstract class MixinWorldGenDungeons {
    @Unique private static final int RDPL$REACH = 4;
    @Unique private static final int RDPL$UNDER = 1;
    @Unique private static final int RDPL$OVER = 4;

    @Inject(method = "generate", at = @At("HEAD"), cancellable = true)
    private void rdpl$notIntoABore(World worldIn, Random rand, BlockPos position, CallbackInfoReturnable<Boolean> cir) {
        List<RailPiece> subways = BeardRails.subways(worldIn, new StructureBoundingBox(position.getX() - RDPL$REACH, 0, position.getZ() - RDPL$REACH, position.getX() + RDPL$REACH, 0, position.getZ() + RDPL$REACH));
        if (subways.isEmpty()) { return; }
        for (int x = position.getX() - RDPL$REACH; x <= position.getX() + RDPL$REACH; x++) {
            for (int z = position.getZ() - RDPL$REACH; z <= position.getZ() + RDPL$REACH; z++) {
                boolean met = false;
                for (int y = position.getY() - RDPL$UNDER; y <= position.getY() + RDPL$OVER && !met; y++) { met = BeardRails.insideBore(worldIn, subways, x, y, z); }
                if (!met) { continue; }
                if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("A dungeon at {}, {}, {} would have opened into a subway bore, so it is left out", position.getX(), position.getY(), position.getZ()); }
                cir.setReturnValue(false);
                return;
            }
        }
    }

    @Redirect(method = "generate", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/MobSpawnerBaseLogic;setEntityId(Lnet/minecraft/util/ResourceLocation;)V"))
    private void rdpl$spawner(MobSpawnerBaseLogic logic, ResourceLocation id, World worldIn, Random rand, BlockPos position) {
        logic.setEntityId(ContentStructurePlacement.spawner(ContentStructurePlacement.DUNGEONS, id, rand));
    }

    @ModifyConstant(method = "generate", constant = @Constant(
            intValue = 0,
            expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO,
            ordinal = 3))
    private int rdpl$getMinHeight(int orig, World worldIn, Random rand, BlockPos position) { return ((IRubicWorld) worldIn).rdpl$getMinHeight(); }
}
