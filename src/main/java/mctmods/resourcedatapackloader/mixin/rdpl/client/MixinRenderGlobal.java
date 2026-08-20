package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.rubic.world.EntityContainer;
import mctmods.resourcedatapackloader.content.rubic.world.cube.BlankCube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

@Mixin(RenderGlobal.class) public class MixinRenderGlobal {
    @Unique @Nullable private BlockPos rdpl$position;
    @Shadow private WorldClient world;

    @Group(name = "renderEntitiesFix", min = 3, max = 3) @Inject(method = "renderEntities",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/WorldClient;getChunk(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/chunk/Chunk;"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onGetPosition(Entity renderViewEntity, ICamera camera, float partialTicks,
                               CallbackInfo ci, int pass, double d0, double d1, double d2,
                               Entity entity, double d3, double d4, double d5,
                               List<Entity> list, List<Entity> list1, List<Entity> list2,
                               BlockPos.PooledMutableBlockPos pos, Iterator<?> var21,
                               @Coerce Object info) {
        RenderChunk renderChunk = ((IContainerLocalRenderInformation) info).getRenderChunk();
        IRubicWorld world = (IRubicWorld) renderChunk.getWorld();
        if (world.rdpl$isRubicWorld()) { this.rdpl$position = renderChunk.getPosition(); }
        else { this.rdpl$position = null; }
    }

    @Dynamic @Group(name = "renderEntitiesFix") @Inject(method = "renderEntities",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/RenderChunk;getChunk()Lnet/minecraft/world/chunk/Chunk;",
                    remap = false),
            locals = LocalCapture.CAPTURE_FAILSOFT, expect = 0)
    private void onGetPosition(Entity renderViewEntity, ICamera camera, float partialTicks,
                               CallbackInfo ci, int pass, double d0, double d1, double d2,
                               Entity entity, double d3, double d4, double d5,
                               List<Entity> list, boolean forgeEntityPass, boolean forgeTileEntityPass, boolean isShaders, boolean oldFancyGraphics, List<Entity> list1, List<Entity> list2,
                               BlockPos.PooledMutableBlockPos pos, Iterator<?> var22,
                               @Coerce Object info) {
        RenderChunk renderChunk = ((IContainerLocalRenderInformation) info).getRenderChunk();
        IRubicWorld world = (IRubicWorld) renderChunk.getWorld();
        if (world.rdpl$isRubicWorld()) { this.rdpl$position = renderChunk.getPosition(); }
        else { this.rdpl$position = null; }
    }

    @Dynamic @Group(name = "renderEntitiesFix") @Inject(method = "renderEntities",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/RenderChunk;getChunk()Lnet/minecraft/world/chunk/Chunk;",
                    remap = false),
            locals = LocalCapture.CAPTURE_FAILSOFT, expect = 0)
    private void onGetPosition(Entity renderViewEntity, ICamera camera, float partialTicks,
                               CallbackInfo ci, int pass, double d0, double d1, double d2,
                               Entity entity, double d3, double d4, double d5,
                               List<Entity> list, boolean forgeEntityPass, boolean forgeTileEntityPass, boolean isShaders, List<Entity> list1, List<Entity> list2,
                               BlockPos.PooledMutableBlockPos pos, boolean playerShadowPass, Iterator<?> var22,
                               @Coerce Object info) {
        RenderChunk renderChunk = ((IContainerLocalRenderInformation) info).getRenderChunk();
        IRubicWorld world = (IRubicWorld) renderChunk.getWorld();
        if (world.rdpl$isRubicWorld()) { this.rdpl$position = renderChunk.getPosition(); }
        else { this.rdpl$position = null; }
    }

    @Group(name = "renderEntitiesFix") @Redirect(method = "renderEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;getY()I"), require = 1)
    private int getRenderChunkYPos(BlockPos pos) {
        if (this.rdpl$position != null) { return 0; }
        return pos.getY() - ((IRubicWorld) world).rdpl$getMinHeight();
    }

    @SuppressWarnings("unchecked") @Group(name = "renderEntitiesFix") @Redirect(method = "renderEntities",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getEntityLists()[Lnet/minecraft/util/ClassInheritanceMultiMap;"),
            require = 1)
    private ClassInheritanceMultiMap<Entity>[] getEntityList(Chunk chunk) {
        if (rdpl$position == null) { return chunk.getEntityLists(); }
        ICube cube = ((IColumn) chunk).getCube(Coords.blockToCube(rdpl$position.getY()));
        if (cube instanceof BlankCube) { return EntityContainer.EMPTY_ARR; }
        return new ClassInheritanceMultiMap[]{cube.getEntitySet()};
    }

    @ModifyConstant(
            method = "renderWorldBorder",
            constant = {
                    @Constant(doubleValue = 0.0D),
                    @Constant(doubleValue = 256.0D) },
            slice = @Slice(from = @At(value = "HEAD"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;draw()V")), require = 2)
    private double renderWorldBorder_getRenderHeight(double original, Entity entityIn, float partialTicks) {
        return original == 0.0D ? entityIn.posY - 128 : entityIn.posY + 128;
    }
}
