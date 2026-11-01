package mctmods.resourcedatapackloader.mixin.world.level;

import mctmods.resourcedatapackloader.common.environment.ContentWeather;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentServer;
import mctmods.resourcedatapackloader.common.pregen.ContentPregen;
import mctmods.resourcedatapackloader.common.rules.ContentRuleSwitch;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class) @Implements(@Interface(iface = LevelAccessor.class, prefix = "level$")) public abstract class LevelMixin {
    @Inject(method = "isRainingAt(Lnet/minecraft/core/BlockPos;)Z", at = @At("HEAD"), cancellable = true) private void rdpl$rainBelowCeiling(BlockPos pos, CallbackInfoReturnable<Boolean> cir) { if (ContentWeather.above((Level) (Object) this, pos.getY())) { cir.setReturnValue(false); } }

    @ModifyExpressionValue(method = "isRainingAt(Lnet/minecraft/core/BlockPos;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;precipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;")) private Biome.Precipitation rdpl$rainWhereSnowIsOff(Biome.Precipitation found, BlockPos pos) {
        Level level = (Level) (Object) this;
        return ContentWeather.fallsAs(level, level.getBiome(pos).value(), found);
    }

    @Inject(method = "canHaveWeather()Z", at = @At("HEAD"), cancellable = true) private void rdpl$cycleHasWeather(CallbackInfoReturnable<Boolean> cir) {
        DimensionTraitsDef traits = ContentWeather.traits((Level) (Object) this);
        if (traits.precipitation() && (traits.cycle() != null || traits.alwaysRain())) { cir.setReturnValue(true); }
    }

    @Nonnull public Difficulty level$getDifficulty() {
        Level self = (Level) (Object) this;
        Difficulty ruled = ContentRuleSwitch.difficulty(self);
        if (ruled != null) { return ruled; }
        Difficulty asked = ContentServer.difficultyFor(self.dimension().identifier().toString());
        return asked != null ? asked : self.getLevelData().getDifficulty();
    }

    @Redirect(method = "tickBlockEntities()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/TickingBlockEntity;tick()V")) private void rdpl$standStillWhileLandIsMade(TickingBlockEntity ticker) { if (((Level) (Object) this).isClientSide() || !ContentPregen.busy()) { ticker.tick(); } }
}
