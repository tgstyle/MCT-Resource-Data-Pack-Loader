package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.interfaces.IPathNodeAsker;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import javax.annotation.Nullable;

@Mixin(WalkNodeProcessor.class)
public abstract class MixinWalkNodeProcessorRaw {
    @Unique private BlockPos.MutableBlockPos rdpl$looking;

    @Redirect(method = "getPathNodeTypeRaw", at = @At(value = "NEW", target = "(III)Lnet/minecraft/util/math/BlockPos;"))
    private BlockPos rdpl$whereItLooks(int x, int y, int z) {
        if (rdpl$looking == null) { rdpl$looking = new BlockPos.MutableBlockPos(); }
        return rdpl$looking.setPos(x, y, z);
    }

    @Redirect(method = {"getPathNodeTypeRaw", "checkNeighborBlocks"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getAiPathNodeType(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/EntityLiving;)Lnet/minecraft/pathfinding/PathNodeType;"))
    @Nullable private PathNodeType rdpl$askBlock(Block block, IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EntityLiving entity) {
        return block.getAiPathNodeType(state, world, rdpl$asks(block) ? new BlockPos(pos) : pos, entity);
    }

    @Unique private static boolean rdpl$asks(Block block) {
        IPathNodeAsker held = (IPathNodeAsker) block;
        byte known = held.rdpl$getAsksWhere();
        if (known != 0) { return known > 0; }

        boolean writes = rdpl$writesItDown(block.getClass());
        held.rdpl$setAsksWhere((byte) (writes ? 1 : -1));
        return writes;
    }

    @Unique private static boolean rdpl$writesItDown(Class<?> block) {
        try {
            return block.getMethod("getAiPathNodeType", IBlockState.class, IBlockAccess.class, BlockPos.class, EntityLiving.class).getDeclaringClass() != Block.class
                    || block.getMethod("getAiPathNodeType", IBlockState.class, IBlockAccess.class, BlockPos.class).getDeclaringClass() != Block.class;
        }
        catch (NoSuchMethodException ex) { return true; }
    }
}
