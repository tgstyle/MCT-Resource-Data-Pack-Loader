package mctmods.resourcedatapackloader.mixin.client.multiplayer;

import mctmods.resourcedatapackloader.client.util.CubeProviderClient;
import mctmods.resourcedatapackloader.common.rubic.lighting.LightingManager;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.core.util.math.IntRange;
import mctmods.resourcedatapackloader.mixin.world.MixinWorld;

import javax.annotation.Nonnull;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;
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
