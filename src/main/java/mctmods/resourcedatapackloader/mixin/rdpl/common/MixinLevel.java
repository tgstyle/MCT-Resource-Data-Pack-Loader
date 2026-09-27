package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentServer;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;

import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import javax.annotation.Nonnull;

@Mixin(Level.class) @Implements(@Interface(iface = LevelAccessor.class, prefix = "level$")) public abstract class MixinLevel {
    @Nonnull public Difficulty level$getDifficulty() {
        Level self = Level.class.cast(this);
        Difficulty asked = ContentServer.difficultyFor(self.dimension().identifier().toString());
        return asked != null ? asked : self.getLevelData().getDifficulty();
    }

    @Redirect(method = "tickBlockEntities()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/TickingBlockEntity;tick()V"))
    private void rdpl$standStillWhileLandIsMade(TickingBlockEntity ticker) {
        if (Level.class.cast(this).isClientSide() || !ContentPregen.busy()) { ticker.tick(); }
    }
}
