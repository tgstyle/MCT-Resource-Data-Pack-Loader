package mctmods.resourcedatapackloader.mixin.quark;

import mctmods.resourcedatapackloader.content.worldgen.ContentCascade;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.world.feature.VariedDungeons;
import java.util.Random;

@Mixin(value = VariedDungeons.class, remap = false)
public abstract class MixinVariedDungeons {
    @Unique private static final int DUNGEON_KINDS = 10;

    @Inject(method = "placeDungeonAt", at = @At("HEAD"), cancellable = true)
    private void rdpl$requireLoaded(WorldServer world, Random rand, BlockPos position, CallbackInfo ci) {
        int reach = 0;
        for (int kind = 0; kind < DUNGEON_KINDS; kind++) {
            BlockPos size = world.getStructureTemplateManager().getTemplate(world.getMinecraftServer(), new ResourceLocation("quark", "dungeon_" + kind)).getSize();
            reach = Math.max(reach, Math.max(size.getX(), size.getZ()));
        }
        if (ContentCascade.loaded(world, position, reach)) { return; }

        rand.nextInt(DUNGEON_KINDS);
        rand.nextInt(Rotation.values().length);
        ci.cancel();
    }

    @Redirect(method = "placeDungeonAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", ordinal = 0, remap = true))
    private IBlockState rdpl$guardSweep(WorldServer world, BlockPos pos) { return ContentCascade.stateOrUnloaded(world, pos); }

    @Inject(method = "couldDungeonGenerate", at = @At("HEAD"), cancellable = true)
    private void rdpl$requireLoadedToLook(World worldIn, Random rand, BlockPos position, CallbackInfoReturnable<Boolean> cir) {
        if (ContentCascade.loaded(worldIn, position, 4)) { return; }

        rand.nextInt(2);
        rand.nextInt(2);
        cir.setReturnValue(false);
    }

    @Redirect(method = "couldDungeonGenerate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", remap = true))
    private IBlockState rdpl$guardLook(World world, BlockPos pos) { return ContentCascade.stateOrUnloaded(world, pos); }

    @Redirect(method = "couldDungeonGenerate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isAirBlock(Lnet/minecraft/util/math/BlockPos;)Z", remap = true))
    private boolean rdpl$guardAir(World world, BlockPos pos) { return ContentCascade.loaded(world, pos) && world.isAirBlock(pos); }
}
