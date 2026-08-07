package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;

import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldServer.class)
public abstract class MixinWorldServerTime {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;setWorldTime(J)V"))
    private void rdpl$holdTimeWhileMaking(WorldServer world, long time) {
        if (ContentPregen.busyIn(world)) { return; }

        world.setWorldTime(time);
    }
}
