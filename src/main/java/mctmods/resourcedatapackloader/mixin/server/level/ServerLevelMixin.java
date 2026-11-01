package mctmods.resourcedatapackloader.mixin.server.level;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.entities.ContentEntities;
import mctmods.resourcedatapackloader.common.entities.behavior.ContentEntityTicks;
import mctmods.resourcedatapackloader.common.environment.ContentTimeWindows;
import mctmods.resourcedatapackloader.common.environment.ContentWeather;
import mctmods.resourcedatapackloader.common.environment.ContentWeatherCycle;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.TimeWindowDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentServer;
import mctmods.resourcedatapackloader.common.pregen.ContentPregen;
import mctmods.resourcedatapackloader.common.rules.ContentGameRules;
import mctmods.resourcedatapackloader.common.rules.ContentRuleSwitch;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.AgeableWaterCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.gamerules.GameRules;
import net.neoforged.neoforge.common.util.ClockAdjustment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class) public abstract class ServerLevelMixin {
    @Unique private final ContentWeatherCycle rdpl$weatherCycle = new ContentWeatherCycle();

    @WrapOperation(method = "advanceWeatherCycle()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;canHaveWeather()Z")) private boolean rdpl$ownWeather(ServerLevel level, Operation<Boolean> original) {
        if (ContentRuleSwitch.forces(level)) {
            ContentWeatherCycle.force(level, ContentRuleSwitch.weather(level));
            return false;
        }
        DimensionDef def = ContentDimensions.def(level);
        DimensionTraitsDef traits = ContentWeather.traits(level);
        DimensionTraitsDef.Cycle cycle = traits.cycle();
        if (!traits.precipitation()) {
            ContentWeatherCycle.clear(level);
            return false;
        }
        TimeWindowDef window = def == null ? null : ContentTimeWindows.active(level, def);
        if (window != null && window.holdsDry()) {
            ContentWeatherCycle.dry(level);
            return false;
        }
        if (window != null && window.gives(TimeWindowDef.ALWAYS_RAIN) ? window.alwaysRain() : traits.alwaysRain()) { return ContentWeatherCycle.pour(level, original.call(level)); }
        if (cycle != null) {
            rdpl$weatherCycle.tick(level, cycle, ContentRuleSwitch.steps(level));
            return false;
        }
        boolean advances = original.call(level);
        if (advances) { ContentWeatherCycle.hurry(level, ContentRuleSwitch.steps(level)); }
        return advances;
    }

    @ModifyExpressionValue(method = "advanceWeatherCycle()V", at = @At(value = "INVOKE", target = "Ljava/lang/Boolean;booleanValue()Z")) private boolean rdpl$followServerWeather(boolean advance) {
        DimensionDef def = ContentDimensions.def((ServerLevel) (Object) this);
        return advance && (def == null || ContentDimensions.existing(def.key()));
    }

    @Inject(method = "tickThunder(Lnet/minecraft/world/level/chunk/LevelChunk;)V", at = @At("HEAD"), cancellable = true) private void rdpl$noLightning(LevelChunk chunk, CallbackInfo ci) {
        DimensionTraitsDef traits = ContentWeather.traits((ServerLevel) (Object) this);
        if (!traits.precipitation() || !traits.lightning()) { ci.cancel(); }
    }

    @Inject(method = "tickPrecipitation(Lnet/minecraft/core/BlockPos;)V", at = @At("HEAD"), cancellable = true) private void rdpl$noPrecipitation(BlockPos pos, CallbackInfo ci) { if (!ContentWeather.traits((ServerLevel) (Object) this).precipitation()) { ci.cancel(); } }

    @Redirect(method = "tickPrecipitation(Lnet/minecraft/core/BlockPos;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;handlePrecipitation(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/biome/Biome$Precipitation;)V")) private void rdpl$rainFillBelowCeiling(Block block, BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) {
        if (ContentWeather.above(level, pos.getY())) { return; }
        Biome.Precipitation falls = ContentWeather.fallsAs(level, level.getBiome(pos.above()).value(), precipitation);
        if (falls == Biome.Precipitation.SNOW && !ContentWeather.traits(level).snow()) { return; }
        block.handlePrecipitation(state, level, pos, falls);
    }

    @WrapOperation(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/util/ClockAdjustment;apply(Lnet/minecraft/world/clock/ServerClockManager;Lnet/minecraft/core/Holder;)V")) private void rdpl$sleepKeepsTime(ClockAdjustment adjustment, ServerClockManager clocks, Holder<WorldClock> clock, Operation<Void> original) { if (rdpl$movesOwnClock((ServerLevel) (Object) this)) { original.call(adjustment, clocks, clock); } }

    @WrapOperation(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;resetWeatherCycle()V")) private void rdpl$sleepKeepsWeather(ServerLevel level, Operation<Void> original) { if (rdpl$movesOwnClock(level)) { original.call(level); } }

    @Unique private static boolean rdpl$movesOwnClock(ServerLevel level) { return level.dimension() == Level.OVERWORLD || ContentDimensions.def(level) == null; }

    @ModifyVariable(method = "explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/util/random/WeightedList;Lnet/minecraft/core/Holder;)V", at = @At("HEAD"), name = "explosionSound", argsOnly = true) private Holder<SoundEvent> rdpl$ownBlastSound(Holder<SoundEvent> explosionSound, @Local(argsOnly = true, name = "source") @Nullable Entity source) {
        SoundEvent own = ContentEntities.explodeSound(source);
        return own != null ? BuiltInRegistries.SOUND_EVENT.wrapAsHolder(own) : explosionSound;
    }

    @Inject(method = "tickNonPassenger", at = @At("HEAD"), cancellable = true) private void rdpl$standStillWhileLandIsMade(Entity entity, CallbackInfo ci) { if (ContentPregen.busy() || ContentEntityTicks.frozen(entity)) { ci.cancel(); } }

    @Redirect(method = "tickNonPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V")) private void rdpl$slowDistantProjectile(Entity entity) {
        if (entity instanceof Projectile && ContentEntityTicks.slowedNow(entity)) { return; }
        entity.tick();
    }

    @Inject(method = "advanceWeatherCycle", at = @At("HEAD"), cancellable = true) private void rdpl$holdWeatherWhileLandIsMade(CallbackInfo ci) { if (ContentPregen.busyIn((ServerLevel) (Object) this)) { ci.cancel(); } }

    @Inject(method = "getGameRules()Lnet/minecraft/world/level/gamerules/GameRules;", at = @At("HEAD"), cancellable = true) private void rdpl$dimensionRules(CallbackInfoReturnable<GameRules> cir) {
        GameRules held = ContentGameRules.forLevel((ServerLevel) (Object) this);
        if (held != null) { cir.setReturnValue(held); }
    }

    @Inject(method = "getCurrentDifficultyAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/DifficultyInstance;", at = @At("RETURN"), cancellable = true) private void rdpl$localDifficulty(BlockPos pos, CallbackInfoReturnable<DifficultyInstance> cir) { cir.setReturnValue(ContentRuleSwitch.local((ServerLevel) (Object) this, pos, cir.getReturnValue())); }

    @Inject(method = "isAllowedToEnterPortal(Lnet/minecraft/world/level/Level;)Z", at = @At("HEAD"), cancellable = true) private void rdpl$nether(Level toLevel, CallbackInfoReturnable<Boolean> cir) { if (toLevel.dimension() == Level.NETHER && ContentServer.off(ContentServer.nether())) { cir.setReturnValue(false); } }

    @Inject(method = "isPvpAllowed()Z", at = @At("HEAD"), cancellable = true) private void rdpl$pvp(CallbackInfoReturnable<Boolean> cir) {
        Boolean asked = ContentServer.pvp();
        if (asked != null) { cir.setReturnValue(asked); }
    }

    @Inject(method = "isCommandBlockEnabled()Z", at = @At("HEAD"), cancellable = true) private void rdpl$commandBlocks(CallbackInfoReturnable<Boolean> cir) { if (ContentServer.off(ContentServer.commandBlocks())) { cir.setReturnValue(false); } }

    @Inject(method = "isSpawningMonsters()Z", at = @At("HEAD"), cancellable = true) private void rdpl$spawnMonsters(CallbackInfoReturnable<Boolean> cir) { if (ContentServer.off(ContentServer.spawnMonsters())) { cir.setReturnValue(false); } }

    @WrapOperation(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/EntityTickList;forEach(Ljava/util/function/Consumer;)V")) private void rdpl$spawnAnimalsAndNpcs(EntityTickList list, Consumer<Entity> output, Operation<Void> original) {
        boolean animals = !ContentServer.off(ContentServer.spawnAnimals());
        boolean npcs = !ContentServer.off(ContentServer.spawnNpcs());
        if (animals && npcs) {
            original.call(list, output);
            return;
        }
        original.call(list, (Consumer<Entity>) entity -> {
            if (!entity.isRemoved() && rdpl$discarded(entity, animals, npcs)) { entity.discard(); }
            else { output.accept(entity); }
        });
    }

    @Unique private static boolean rdpl$discarded(Entity entity, boolean animals, boolean npcs) {
        if (!animals && (entity instanceof Animal || entity instanceof WaterAnimal || entity instanceof AgeableWaterCreature)) { return true; }
        return !npcs && entity instanceof Npc;
    }
}
