package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntityTicks;
import mctmods.resourcedatapackloader.content.entity.ai.PathNodeMemo;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IMinMaxHeight;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.NodeProcessor;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WalkNodeProcessor.class) public abstract class MixinWalkNodeProcessor extends NodeProcessor {
    @Unique private PathNodeMemo rdpl$memo;
    @Unique private boolean rdpl$located;
    @Unique private int rdpl$world;
    @Unique private int rdpl$owner;
    @Unique private BlockPos.MutableBlockPos rdpl$standing;
    @Unique private BlockPos.MutableBlockPos rdpl$underfoot;
    @Shadow @NotNull public abstract PathNodeType getPathNodeType(@NotNull IBlockAccess blockaccessIn, int x, int y, int z);

    @Redirect(method = "getPathNodeType(Lnet/minecraft/world/IBlockAccess;IIILnet/minecraft/entity/EntityLiving;IIIZZ)Lnet/minecraft/pathfinding/PathNodeType;",
            at = @At(value = "NEW", target = "(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/BlockPos;"))
    private BlockPos rdpl$whereItStands(Entity source) {
        if (rdpl$standing == null) { rdpl$standing = new BlockPos.MutableBlockPos(); }
        return rdpl$standing.setPos(source);
    }

    @Redirect(method = "getPathNodeType(Lnet/minecraft/world/IBlockAccess;III)Lnet/minecraft/pathfinding/PathNodeType;",
            at = @At(value = "NEW", target = "(III)Lnet/minecraft/util/math/BlockPos;"))
    private BlockPos rdpl$whatIsUnderfoot(int x, int y, int z) {
        if (rdpl$underfoot == null) { rdpl$underfoot = new BlockPos.MutableBlockPos(); }
        return rdpl$underfoot.setPos(x, y, z);
    }

    @Redirect(method = "getPathNodeType(Lnet/minecraft/world/IBlockAccess;IIIIIIZZLjava/util/EnumSet;Lnet/minecraft/pathfinding/PathNodeType;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/pathfinding/PathNodeType;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/pathfinding/WalkNodeProcessor;getPathNodeType(Lnet/minecraft/world/IBlockAccess;III)Lnet/minecraft/pathfinding/PathNodeType;"))
    private PathNodeType rdpl$rememberKind(WalkNodeProcessor self, IBlockAccess blockaccessIn, int x, int y, int z) {
        if (!rdpl$located) {
            World reading = rdpl$worldOf(blockaccessIn);
            if (reading == null) { return getPathNodeType(blockaccessIn, x, y, z); }
            rdpl$world = reading.provider.getDimension();
            rdpl$owner = System.identityHashCode(getClass());
            rdpl$memo = PathNodeMemo.held();
            rdpl$located = true;
        }
        int tick = ContentEntityTicks.ticks();
        PathNodeType held = rdpl$memo.known(rdpl$owner, rdpl$world, tick, x, y, z);
        if (held != null) { return held; }
        PathNodeType found = getPathNodeType(blockaccessIn, x, y, z);
        rdpl$memo.remember(rdpl$owner, rdpl$world, tick, x, y, z, found);
        return found;
    }

    @Unique @Nullable private static World rdpl$worldOf(IBlockAccess reading) {
        if (reading instanceof World) { return (World) reading; }
        if (reading instanceof ChunkCache) { return ((IChunkCache) reading).rdpl$getWorld(); }
        return null;
    }

    @ModifyConstant(method = "getStart", constant = @Constant(intValue = 0, expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO)) private int getMinHeight_GetStart(int originalY) {
        return ((IRubicWorld) this.entity.world).rdpl$getMinHeight() + originalY;
    }

    @Redirect(method = "getStart", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/IBlockAccess;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;"))
    private IBlockState getLoadedBlockState_getStart(IBlockAccess access, BlockPos pos) {
        if (!entity.world.isBlockLoaded(pos)) { return Objects.requireNonNull(Blocks.BEDROCK).getDefaultState(); }
        return access.getBlockState(pos);
    }

    @ModifyConstant(
            method = "getSafePoint",
            constant = @Constant(
                    expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO,
                    ordinal = 1
            ))
    private int getMinHeight_GetSafePoint(int originalY) { return ((IRubicWorld) this.entity.world).rdpl$getMinHeight() + originalY; }

    @ModifyConstant(
            method = "getPathNodeType(Lnet/minecraft/world/IBlockAccess;III)Lnet/minecraft/pathfinding/PathNodeType;",
            constant = @Constant(
                    intValue = 1, ordinal = 0
            ))
    private int getMinHeight_GetPathNodeType(int originalY, IBlockAccess blockaccessIn, int x, int y, int z) {
        return ((IMinMaxHeight) blockaccessIn).rdpl$getMinHeight() + originalY;
    }
}
