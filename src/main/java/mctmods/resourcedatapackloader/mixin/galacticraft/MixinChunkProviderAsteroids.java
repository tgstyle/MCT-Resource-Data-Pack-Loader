package mctmods.resourcedatapackloader.mixin.galacticraft;

import mctmods.resourcedatapackloader.util.compat.GcWorldProvider;

import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.ChunkProviderAsteroids;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.objectweb.asm.Opcodes;
import java.util.HashSet;
import java.util.Set;

@Mixin(value = ChunkProviderAsteroids.class, remap = false) public class MixinChunkProviderAsteroids {
    @Unique private final Set<Object> rdpl$chunksDone = new HashSet<>();

    @Redirect(method = "populate(II)V", at = @At(value = "INVOKE", target = "Ljava/util/HashSet;add(Ljava/lang/Object;)Z"))
    private boolean rdpl$ownChunksDone(HashSet<Object> shared, Object chunk) { return rdpl$chunksDone.add(chunk); }

    @Redirect(method = "generateAsteroid(Ljava/util/Random;IIIIIILnet/minecraft/world/chunk/ChunkPrimer;Z)V", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;provider:Lnet/minecraft/world/WorldProvider;", opcode = Opcodes.GETFIELD, remap = true))
    private WorldProvider rdpl$packBelt(World world) { return world.provider instanceof GcWorldProvider ? ((GcWorldProvider) world.provider).asteroids() : world.provider; }
}
