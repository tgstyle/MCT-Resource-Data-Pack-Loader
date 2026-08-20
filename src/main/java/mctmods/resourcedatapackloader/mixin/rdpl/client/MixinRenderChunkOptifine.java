package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.core.optifine.interfaces.IOptifineRenderChunk;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("target") @Mixin(RenderChunk.class) @Implements(@Interface(iface = IOptifineRenderChunk.class, prefix = "rubic$")) public abstract class MixinRenderChunkOptifine {
    @Shadow @Final private BlockPos.MutableBlockPos position;
    @Shadow private World world;
    @Dynamic @Shadow(remap = false) private RenderChunk[] renderChunkNeighboursValid;
    @Dynamic @Shadow(remap = false) private RenderChunk[] renderChunkNeighbours;
    @Unique private int rdpl$regionY;
    @Dynamic @Shadow(remap = false) private int regionX;

    @Shadow public abstract BlockPos getPosition();

    @Unique private ICube rdpl$cube;
    @Unique private boolean rdpl$isRubic;

    @Inject(method = "<init>", at = @At("RETURN")) private void onConstruct(World worldIn, RenderGlobal renderGlobalIn, int indexIn, CallbackInfo cbi) {
        this.rdpl$isRubic = ((IRubicWorld) worldIn).rdpl$isRubicWorld();
    }

    @Dynamic @Inject(method = "setPosition",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/chunk/RenderChunk;chunk:Lnet/minecraft/world/chunk/Chunk;",
                    opcode = Opcodes.PUTFIELD,
                    remap = false)
    )
    private void onSetChunk(int x, int y, int z, CallbackInfo cbi) {
        this.rdpl$cube = null;
        this.rdpl$isRubic = ((IRubicWorld) world).rdpl$isRubicWorld();
        this.rdpl$regionY = y & ~255;
    }

    @Dynamic @Inject(method = "updateRenderChunkNeighboursValid()V", at = @At("HEAD"), remap = false)
    private void onUpdateNeighbors(CallbackInfo cbi) {
        if (!rdpl$isRubic) { return; }
        int y = this.getPosition().getY();
        int up = EnumFacing.UP.ordinal();
        int down = EnumFacing.DOWN.ordinal();
        RenderChunk upNeighbor = this.renderChunkNeighbours[up];
        RenderChunk downNeighbor = this.renderChunkNeighbours[down];
        this.renderChunkNeighboursValid[up] = upNeighbor.getPosition().getY() == y + 16 ? upNeighbor : null;
        this.renderChunkNeighboursValid[down] = downNeighbor.getPosition().getY() == y - 16 ? downNeighbor : null;
    }

    public ICube rubic$getCube() { return this.rdpl$getCube(this.position); }

    public boolean rubic$isRubic() { return rdpl$isRubic; }

    public int rubic$getRegionY() { return this.rdpl$regionY; }

    public int rubic$getRegionX() { return this.regionX; }

    @Unique private ICube rdpl$getCube(BlockPos posIn) {
        ICube cubeLocal = this.rdpl$cube;
        if (cubeLocal == null || !cubeLocal.isCubeLoaded()) {
            cubeLocal = ((IRubicWorld) this.world).rdpl$getCubeFromBlockCoords(posIn);
            this.rdpl$cube = cubeLocal;
        }
        return cubeLocal;
    }
}
