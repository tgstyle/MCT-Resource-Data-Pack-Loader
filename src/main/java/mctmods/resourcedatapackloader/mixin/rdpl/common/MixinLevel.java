package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentGameRules;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;

import net.minecraft.world.Difficulty;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import javax.annotation.Nonnull;

@Mixin(Level.class) @Implements(@Interface(iface = LevelAccessor.class, prefix = "level$")) public abstract class MixinLevel {
    @Nonnull public Difficulty level$getDifficulty() {
        Level self = Level.class.cast(this);
        Difficulty asked = ContentTerrain.difficultyFor(self.dimension().location().toString());
        return asked != null ? asked : self.getLevelData().getDifficulty();
    }

    public long level$dayTime() { return Level.class.cast(this).getDayTime(); }

    @Inject(method = "getDayTime", at = @At("HEAD"), cancellable = true)
    private void rdpl$lockedTime(CallbackInfoReturnable<Long> cir) {
        long locked = ContentTerrain.lockedTime(Level.class.cast(this));
        if (locked >= 0) { cir.setReturnValue(locked); }
    }

    @Inject(method = "getGameRules", at = @At("HEAD"), cancellable = true)
    private void rdpl$dimensionRules(CallbackInfoReturnable<GameRules> cir) {
        GameRules held = ContentGameRules.forLevel(Level.class.cast(this));
        if (held != null) { cir.setReturnValue(held); }
    }

    @Redirect(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/TickingBlockEntity;tick()V"))
    private void rdpl$standStillWhileLandIsMade(TickingBlockEntity ticker) {
        if (Level.class.cast(this).isClientSide() || !ContentPregen.busy()) { ticker.tick(); }
    }
}
