package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensionRules;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldProvider.class) public abstract class MixinWorldProviderContent {
    @Shadow protected BiomeProvider biomeProvider;
    @Shadow protected boolean doesWaterVaporize;
    @Shadow protected boolean nether;
    @Shadow protected boolean hasSkyLight;
    @Shadow @Final protected float[] lightBrightnessTable;

    @Inject(method = "setWorld", at = @At("RETURN")) private void rdpl$rememberDimension(World worldIn, CallbackInfo ci) {
        ContentBiomeControl.remember(worldIn);
        WorldProvider self = (WorldProvider) (Object) this;
        DimensionDef def = ContentDimensions.overlay(self.getDimension());
        if (def == null) { return; }
        if (def.gives("sky.hasSkyLight")) { hasSkyLight = def.hasSkyLight; }
        if (def.gives("sky.nether")) { nether = def.nether; }
        if (def.gives("sky.waterVaporizes")) { doesWaterVaporize = def.waterVaporizes; }
        if (def.gives("biomes")) { biomeProvider = ContentDimensionRules.biomes(worldIn, def); }
        if (def.gives("sky.ambientLight")) { ContentDimensionRules.lightTable(def.ambientLight, lightBrightnessTable); }
        ContentDimensionRules.renderers(worldIn, self, def);
    }

    @Inject(method = "getWorldTime", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$lockedTime(CallbackInfoReturnable<Long> cir) {
        int wanted = rdpl$wanted();
        if (wanted < 0) { return; }
        cir.setReturnValue((long) wanted);
    }

    @Inject(method = "isDaytime", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$lockedDaytime(CallbackInfoReturnable<Boolean> cir) {
        int wanted = rdpl$wanted();
        if (wanted < 0) { return; }
        long time = wanted % DimensionTraitsDef.VANILLA_DAY;
        cir.setReturnValue(time < 12300L || time > 23850L);
    }

    @Unique private int rdpl$wanted() {
        WorldProvider self = (WorldProvider) (Object) this;
        if (self.getDimension() != 0) { return -1; }
        return ContentTerrain.worldTime();
    }
}
