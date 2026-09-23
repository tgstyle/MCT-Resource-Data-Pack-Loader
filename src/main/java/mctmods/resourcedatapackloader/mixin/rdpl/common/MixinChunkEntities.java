package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumnInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.util.Coords;
import static mctmods.resourcedatapackloader.util.Coords.blockToCube;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
import javax.annotation.Nullable;

@Mixin(value = Chunk.class, priority = 999)
public abstract class MixinChunkEntities {
    @Shadow @Final private World world;
    @Shadow private boolean loaded;

    @Unique @SuppressWarnings("unchecked") private <T extends World & IRubicWorldInternal> T rdpl$getRubicWorld() { return (T) this.world; }

    @Unique private boolean rdpl$isColumn() { return ((IColumnInternal) this).isRubicColumn(); }

    @Unique private boolean rdpl$compatGenerating() { return ((IColumnInternal) this).getCompatGenerationPrimer() != null; }

    @Unique private boolean rdpl$cubeLoadedAt(int blockY) {
        ICube cube = ((IColumn) this).getLoadedCube(blockToCube(blockY));
        return cube != null && cube.isCubeLoaded();
    }

    @Unique private int rdpl$clampCubeY(int cubeY) { return MathHelper.clamp(cubeY, blockToCube(rdpl$getRubicWorld().rdpl$getMinHeight()), blockToCube(rdpl$getRubicWorld().rdpl$getMaxHeight())); }

    @Unique @SuppressWarnings("unchecked") private Iterable<Cube> rdpl$loadedCubes(int startY, int endY) { return (Iterable<Cube>) ((IColumn) this).getLoadedCubes(startY, endY); }

    @ModifyConstant(method = "addEntity",
            constant = @Constant(expandZeroConditions = Constant.Condition.LESS_THAN_ZERO, intValue = 0),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE:LAST",
                            target = "Lnet/minecraft/util/math/MathHelper;floor(D)I"),
                    to = @At(
                            value = "FIELD:FIRST",
                            target = "Lnet/minecraft/world/chunk/Chunk;entityLists:[Lnet/minecraft/util/ClassInheritanceMultiMap;",
                            opcode = Opcodes.GETFIELD)
            ),
            require = 1
    )
    private int addEntity_getMinY(int zero) { return blockToCube(rdpl$getRubicWorld().rdpl$getMinHeight()); }

    @Redirect(method = "addEntity",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD, args = "array=length",
                    target = "Lnet/minecraft/world/chunk/Chunk;entityLists:[Lnet/minecraft/util/ClassInheritanceMultiMap;"
            ),
            require = 2)
    private int addEntity_getMaxHeight(ClassInheritanceMultiMap<?>[] entityLists) {
        return rdpl$isColumn() ? blockToCube(rdpl$getRubicWorld().rdpl$getMaxHeight()) : (entityLists.length - Coords.blockToCube(rdpl$getRubicWorld().rdpl$getMinHeight()));
    }

    @Redirect(method = "addEntity",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD, args = "array=get",
                    target = "Lnet/minecraft/world/chunk/Chunk;entityLists:[Lnet/minecraft/util/ClassInheritanceMultiMap;"
            ),
            require = 1)
    private ClassInheritanceMultiMap<?> addEntity_getEntityList(ClassInheritanceMultiMap<?>[] entityLists, int idx, Entity entityIn) {
        if (!rdpl$isColumn()) { return entityLists[idx - Coords.blockToCube(rdpl$getRubicWorld().rdpl$getMinHeight())]; }
        ((Cube) ((IColumn) this).getCube(idx)).getEntityContainer().addEntity(entityIn);
        return null;
    }

    @Redirect(method = "addEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/ClassInheritanceMultiMap;add(Ljava/lang/Object;)Z"
            ),
            require = 1)
    private boolean addEntity_getEntityList(ClassInheritanceMultiMap<Object> obj, Object p_add_1_) {
        if (!rdpl$isColumn()) { return obj.add(p_add_1_); }
        assert obj == null;
        return true;
    }

    @ModifyConstant(method = "removeEntityAtIndex",
            constant = @Constant(expandZeroConditions = Constant.Condition.LESS_THAN_ZERO, intValue = 0),
            require = 2,
            slice = @Slice(
                    from = @At("HEAD"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/util/ClassInheritanceMultiMap;remove(Ljava/lang/Object;)Z")
            )
    )
    private int removeEntityAtIndex_getMinY(int zero) { return blockToCube(rdpl$getRubicWorld().rdpl$getMinHeight()); }

    @Redirect(method = "removeEntityAtIndex",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD, args = "array=length",
                    target = "Lnet/minecraft/world/chunk/Chunk;entityLists:[Lnet/minecraft/util/ClassInheritanceMultiMap;"
            ),
            require = 2)
    private int removeEntityAtIndex_getMaxHeight(ClassInheritanceMultiMap<?>[] entityLists) {
        return rdpl$isColumn() ? blockToCube(rdpl$getRubicWorld().rdpl$getMaxHeight()) : (entityLists.length - Coords.blockToCube(rdpl$getRubicWorld().rdpl$getMinHeight()));
    }

    @Redirect(method = "removeEntityAtIndex",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD, args = "array=get",
                    target = "Lnet/minecraft/world/chunk/Chunk;entityLists:[Lnet/minecraft/util/ClassInheritanceMultiMap;"
            ),
            require = 1)
    private ClassInheritanceMultiMap<?> removeEntityAtIndex_getEntityList(ClassInheritanceMultiMap<?>[] entityLists, int idx, Entity entityIn,
                                                                          int index) {
        if (!rdpl$isColumn()) { return entityLists[idx - Coords.blockToCube(rdpl$getRubicWorld().rdpl$getMinHeight())]; }
        ((Cube) ((IColumn) this).getCube(idx)).getEntityContainer().remove(entityIn);
        return null;
    }

    @Redirect(method = "removeEntityAtIndex",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/ClassInheritanceMultiMap;remove(Ljava/lang/Object;)Z"
            ),
            require = 1)
    private boolean removeEntityAtIndex_getEntityList(ClassInheritanceMultiMap<Object> obj, Object p_remove_1_) {
        if (!rdpl$isColumn()) { return obj.remove(p_remove_1_); }
        assert obj == null;
        return true;
    }

    @Inject(method = "getTileEntity", at = @At("HEAD"), cancellable = true)
    private void getTileEntity_CompatTemplate(BlockPos pos, Chunk.EnumCreateEntityType creationMode, CallbackInfoReturnable<TileEntity> cir) {
        if (rdpl$compatGenerating()) { cir.setReturnValue(null); }
    }

    @Inject(method = "addTileEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/tileentity/TileEntity;)V", at = @At("HEAD"), cancellable = true)
    private void addTileEntity_CompatTemplate(BlockPos pos, TileEntity tileEntityIn, CallbackInfo cbi) {
        if (rdpl$compatGenerating()) { cbi.cancel(); }
    }

    @Redirect(method = "addTileEntity(Lnet/minecraft/tileentity/TileEntity;)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/chunk/Chunk;loaded:Z", opcode = Opcodes.GETFIELD))
    private boolean addTileEntity_isChunkLoadedCubeRedirect(Chunk chunk, TileEntity tileEntityIn) {
        if (!rdpl$isColumn()) { return loaded; }
        return rdpl$cubeLoadedAt(tileEntityIn.getPos().getY());
    }

    @Redirect(method = "removeTileEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/world/chunk/Chunk;loaded:Z", opcode = Opcodes.GETFIELD)) private boolean removeTileEntity_isChunkLoadedCubeRedirect(Chunk chunk, BlockPos pos) {
        if (!rdpl$isColumn()) { return loaded; }
        return rdpl$cubeLoadedAt(pos.getY());
    }

    @Inject(method = "getEntitiesWithinAABBForEntity", at = @At("HEAD"), cancellable = true) private void getEntitiesWithinAABBForEntity_Rubic(@Nullable Entity entityIn, AxisAlignedBB aabb,
                                                                                                                                               List<Entity> listToFill, Predicate<? super Entity> filter, CallbackInfo cbi) {
        if (!rdpl$isColumn()) { return; }
        cbi.cancel();
        int minY = MathHelper.floor((aabb.minY - World.MAX_ENTITY_RADIUS) / Cube.SIZE_D);
        int maxY = MathHelper.floor((aabb.maxY + World.MAX_ENTITY_RADIUS) / Cube.SIZE_D);
        minY = rdpl$clampCubeY(minY);
        maxY = rdpl$clampCubeY(maxY);
        for (Cube cube : rdpl$loadedCubes(minY, maxY)) {
            if (cube.getEntityContainer().getEntitySet().isEmpty()) { continue; }
            for (Entity entity : cube.getEntityContainer().getEntitySet()) {
                if (!entity.getEntityBoundingBox().intersects(aabb) || entity == entityIn) { continue; }
                if (filter == null || filter.apply(entity)) { listToFill.add(entity); }
                Entity[] parts = entity.getParts();
                if (parts != null) {
                    for (Entity part : parts) {
                        if (part != entityIn && part.getEntityBoundingBox().intersects(aabb)
                                && (filter == null || filter.apply(part))) { listToFill.add(part); }
                    }
                }
            }
        }
    }

    @Inject(method = "getEntitiesOfTypeWithinAABB", at = @At("HEAD"), cancellable = true) private <T extends Entity> void getEntitiesOfTypeWithinAAAB_Rubic(Class<? extends T> entityClass,
                                                                                                                                                            AxisAlignedBB aabb, List<T> listToFill, Predicate<? super T> filter, CallbackInfo cbi) {
        if (!rdpl$isColumn()) { return; }
        cbi.cancel();
        int minY = MathHelper.floor((aabb.minY - World.MAX_ENTITY_RADIUS) / Cube.SIZE_D);
        int maxY = MathHelper.floor((aabb.maxY + World.MAX_ENTITY_RADIUS) / Cube.SIZE_D);
        minY = rdpl$clampCubeY(minY);
        maxY = rdpl$clampCubeY(maxY);
        for (Cube cube : rdpl$loadedCubes(minY, maxY)) {
            for (T t : cube.getEntityContainer().getEntitySet().getByClass(entityClass)) {
                if (t.getEntityBoundingBox().intersects(aabb) && (filter == null || filter.apply(t))) { listToFill.add(t); }
            }
        }
    }

    @Redirect(method = "removeInvalidTileEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/world/chunk/Chunk;loaded:Z", opcode = Opcodes.GETFIELD)) private boolean removeInvalidTileEntity_isChunkLoadedCubeRedirect(Chunk chunk, BlockPos pos) {
        if (!rdpl$isColumn()) { return loaded; }
        return rdpl$cubeLoadedAt(pos.getY());
    }
}
