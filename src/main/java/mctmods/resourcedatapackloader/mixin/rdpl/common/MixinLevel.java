package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentServer;
import mctmods.resourcedatapackloader.content.worldgen.ContentGameRules;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentRuleSwitch;
import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.content.worldgen.ContentWeather;
import mctmods.resourcedatapackloader.util.interfaces.IDayLength;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import javax.annotation.Nonnull;

@Mixin(Level.class) @Implements(@Interface(iface = LevelAccessor.class, prefix = "level$")) public abstract class MixinLevel {
    @Nonnull public Difficulty level$getDifficulty() {
        Level self = (Level) (Object) this;
        Difficulty ruled = ContentRuleSwitch.difficulty(self);
        if (ruled != null) { return ruled; }
        Difficulty asked = ContentServer.difficultyFor(self.dimension().location().toString());
        return asked != null ? asked : self.getLevelData().getDifficulty();
    }

    public long level$dayTime() { return ((Level) (Object) this).getDayTime(); }

    @Inject(method = "getDayTime", at = @At("HEAD"), cancellable = true) private void rdpl$lockedTime(CallbackInfoReturnable<Long> cir) {
        long locked = ContentTerrain.lockedTime((Level) (Object) this);
        if (locked >= 0) { cir.setReturnValue(locked); }
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/storage/WritableLevelData;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/core/Holder;Ljava/util/function/Supplier;ZZJI)V", at = @At("RETURN")) private void rdpl$dayLength(CallbackInfo ci) {
        Level self = (Level) (Object) this;
        IDayLength.class.cast(self.dimensionType()).rdpl$dayLength(ContentWeather.traits(self).dayLength());
        ContentRuleSwitch.settle(self);
    }

    @Inject(method = "getCurrentDifficultyAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/DifficultyInstance;", at = @At("RETURN"), cancellable = true) private void rdpl$ruledDifficulty(BlockPos pos, CallbackInfoReturnable<DifficultyInstance> cir) { if ((Object) this instanceof ServerLevel level) { cir.setReturnValue(ContentRuleSwitch.local(level, pos, cir.getReturnValue())); } }

    @Inject(method = "getGameRules", at = @At("HEAD"), cancellable = true) private void rdpl$dimensionRules(CallbackInfoReturnable<GameRules> cir) {
        GameRules held = ContentGameRules.forLevel((Level) (Object) this);
        if (held != null) { cir.setReturnValue(held); }
    }

    @Redirect(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/TickingBlockEntity;tick()V")) private void rdpl$standStillWhileLandIsMade(TickingBlockEntity ticker) { if (((Level) (Object) this).isClientSide() || !ContentPregen.busy()) { ticker.tick(); } }
}
