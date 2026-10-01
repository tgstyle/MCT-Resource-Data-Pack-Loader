package mctmods.resourcedatapackloader.mixin.galacticraft;

import mctmods.resourcedatapackloader.util.compat.GcWorldProvider;

import micdoodle8.mods.galacticraft.planets.asteroids.dimension.TeleportTypeAsteroids;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.objectweb.asm.Opcodes;

@Mixin(value = TeleportTypeAsteroids.class, remap = false) public class MixinTeleportTypeAsteroids {
    @Redirect(method = "getPlayerSpawnLocation(Lnet/minecraft/world/WorldServer;Lnet/minecraft/entity/player/EntityPlayerMP;)Lmicdoodle8/mods/galacticraft/api/vector/Vector3;", at = @At(value = "FIELD", target = "Lnet/minecraft/world/WorldServer;provider:Lnet/minecraft/world/WorldProvider;", opcode = Opcodes.GETFIELD, remap = true))
    private WorldProvider rdpl$packBelt(WorldServer world) { return world.provider instanceof GcWorldProvider ? ((GcWorldProvider) world.provider).asteroids() : world.provider; }
}
