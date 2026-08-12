package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.entity.ContentEntityTicks;
import mctmods.resourcedatapackloader.content.entity.ai.PathNodeMemo;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import javax.annotation.Nullable;

@Mixin(WalkNodeProcessor.class)
public abstract class MixinWalkNodeProcessor {
    @Unique private PathNodeMemo rdpl$memo;
    @Unique private boolean rdpl$located;
    @Unique private int rdpl$world;
    @Unique private int rdpl$owner;
    @Unique private BlockPos.MutableBlockPos rdpl$standing;
    @Unique private BlockPos.MutableBlockPos rdpl$underfoot;

    @Shadow public abstract PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z);

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
        if (reading instanceof ChunkCache) { return ((AccessorChunkCache) reading).rdpl$getWorld(); }

        return null;
    }

}
