package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.RubicWorldControl;
import mctmods.resourcedatapackloader.content.rubic.world.SpawnPlaceFinder;
import mctmods.resourcedatapackloader.content.rubic.world.WorldSavedRubicData;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldProvider;
import mctmods.resourcedatapackloader.content.rubic.worldgen.VanillaCompatibilityGeneratorProviderBase;
import mctmods.resourcedatapackloader.content.rubic.worldgen.interfaces.ICubeGenerator;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.util.NotRubicWorldException;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import javax.annotation.Nonnull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.IChunkGenerator;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import java.util.Objects;
import javax.annotation.Nullable;

@Mixin(WorldProvider.class) public abstract class MixinWorldProvider implements IRubicWorldProvider {
    @Inject(method = "setWorld", at = @At("RETURN")) private void rdpl$rememberDimension(World worldIn, CallbackInfo ci) { ContentBiomeControl.remember(worldIn); }

    @Inject(method = "getWorldTime", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$lockedTime(CallbackInfoReturnable<Long> cir) {
        int wanted = rdpl$wanted();
        if (wanted < 0) { return; }
        cir.setReturnValue((long) wanted);
    }

    @Inject(method = "isDaytime", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$lockedDaytime(CallbackInfoReturnable<Boolean> cir) {
        int wanted = rdpl$wanted();
        if (wanted < 0) { return; }
        long time = wanted % 24000L;
        cir.setReturnValue(time < 12300L || time > 23850L);
    }

    @Unique private int rdpl$wanted() {
        WorldProvider self = (WorldProvider) (Object) this;
        if (self.getDimension() != 0) { return -1; }
        return ContentTerrain.worldTime();
    }

    @Shadow protected World world;
    @Shadow protected boolean nether;

    @Shadow public abstract IChunkGenerator createChunkGenerator();

    @Shadow(remap = false) public abstract int getActualHeight();

    /**
     * @author tgstyle
     * @reason Report the rubic world height instead of the fixed vanilla provider height.
     */
    @Overwrite(remap = false) public int getHeight() { return ((IRubicWorld) world).rdpl$getMaxHeight(); }

    @Inject(method = "getActualHeight", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$rubicActualHeight(CallbackInfoReturnable<Integer> cir) {
        IRubicWorld rubic = (IRubicWorld) world;
        if (rubic.rdpl$isRubicWorld()) { cir.setReturnValue(Math.min(rubic.rdpl$getMaxHeight(), rubic.rdpl$getMaxGenerationHeight() + (RubicWorldControl.terrainOffsetCubes() << 4))); }
    }

    @Override public int rdpl$getOriginalActualHeight() {
        if (((IRubicWorld) world).rdpl$isRubicWorld()) { return ((IRubicWorld) world).rdpl$getMaxGenerationHeight(); }
        return getActualHeight();
    }

    @Nullable @Override public ICubeGenerator rdpl$createCubeGenerator() {
        if (!((IRubicWorld) world).rdpl$isRubicWorld()) { throw new NotRubicWorldException(); }
        WorldSavedRubicData savedData = Objects.requireNonNull(
                (WorldSavedRubicData) world.getPerWorldStorage().getOrLoadData(WorldSavedRubicData.class, "rdplRubicData"),
                "rdplRubicData missing for rubic world");
        return Objects.requireNonNull(
                        VanillaCompatibilityGeneratorProviderBase.REGISTRY.getValue(savedData.compatibilityGeneratorType),
                        "unregistered compatibility generator " + savedData.compatibilityGeneratorType)
                .provideGenerator(this.createChunkGenerator(), world);
    }

    @Inject(method = "getRandomizedSpawnPoint", at = @At(value = "HEAD"), cancellable = true, remap = false) private void findRandomizedSpawnPoint(CallbackInfoReturnable<BlockPos> cir) {
        if (((IRubicWorld) world).rdpl$isRubicWorld()) { cir.setReturnValue(SpawnPlaceFinder.getRandomizedSpawnPoint(world)); }
    }

    @Inject(method = "canCoordinateBeSpawn", at = @At("HEAD"), cancellable = true) private void canCoordinateBeSpawnRubic(int x, int z, CallbackInfoReturnable<Boolean> cir) {
        if (!((IRubicWorld) world).rdpl$isRubicWorld()) { return; }
        BlockPos blockpos = new BlockPos(x, 64, z);
        if (this.world.getBiome(blockpos).ignorePlayerSpawnSuitability()) { cir.setReturnValue(true); }
        else {
            BlockPos top = SpawnPlaceFinder.getTopBlockBisect(world, blockpos);
            if (top == null) { cir.setReturnValue(false); }
            else { cir.setReturnValue(this.world.getBlockState(top).getBlock() == Blocks.GRASS); }
        }
    }

    @Override @Nonnull public World rdpl$getWorld() { return world; }
}
