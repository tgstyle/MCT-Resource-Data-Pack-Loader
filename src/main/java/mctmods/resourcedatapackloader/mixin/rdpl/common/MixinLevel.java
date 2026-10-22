package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentServer;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentRuleSwitch;

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
        Level self = (Level) (Object) this;
        Difficulty ruled = ContentRuleSwitch.difficulty(self);
        if (ruled != null) { return ruled; }
        Difficulty asked = ContentServer.difficultyFor(self.dimension().identifier().toString());
        return asked != null ? asked : self.getLevelData().getDifficulty();
    }

    @Redirect(method = "tickBlockEntities()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/TickingBlockEntity;tick()V")) private void rdpl$standStillWhileLandIsMade(TickingBlockEntity ticker) { if (((Level) (Object) this).isClientSide() || !ContentPregen.busy()) { ticker.tick(); } }
}
