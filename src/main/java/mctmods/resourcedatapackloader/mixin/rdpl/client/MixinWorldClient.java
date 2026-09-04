package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.CubeProviderClient;
import mctmods.resourcedatapackloader.content.rubic.lighting.LightingManager;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.mixin.rdpl.common.MixinWorld;
import mctmods.resourcedatapackloader.util.IntRange;

import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;
import javax.annotation.Nonnull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldClient.class) public abstract class MixinWorldClient extends MixinWorld implements IRubicWorldInternal.IClient {
    @Shadow private ChunkProviderClient clientChunkProvider;

    @Override public void rdpl$initRubicWorldClient(IntRange heightRange, IntRange generationRange) {
        super.rdpl$initRubicWorld(heightRange, generationRange);
        this.rdpl$isRubicWorld = true;
        CubeProviderClient cubeProviderClient = new CubeProviderClient(this);
        this.chunkProvider = cubeProviderClient;
        this.clientChunkProvider = cubeProviderClient;
        this.rdpl$lightingManager = new LightingManager((World) (Object) this);
    }

    @Override public void rdpl$tickRubicWorld() { rdpl$getLightingManager().onTick(); }

    @Override @Nonnull public CubeProviderClient rdpl$getCubeCache() { return (CubeProviderClient) this.clientChunkProvider; }
}
