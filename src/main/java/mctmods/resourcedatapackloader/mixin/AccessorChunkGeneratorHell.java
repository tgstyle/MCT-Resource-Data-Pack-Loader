package mctmods.resourcedatapackloader.mixin;

import net.minecraft.world.gen.ChunkGeneratorHell;
import net.minecraft.world.gen.structure.MapGenNetherBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkGeneratorHell.class)
public interface AccessorChunkGeneratorHell {
    @Accessor("genNetherBridge") MapGenNetherBridge rdpl$fortresses();
}
