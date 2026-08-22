package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IMinMaxHeight;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(PathNavigateGround.class) public abstract class MixinPathNavigateGround extends PathNavigate {
    public MixinPathNavigateGround(EntityLiving entitylivingIn, World worldIn) { super(entitylivingIn, worldIn); }

    /**
     * @author tgstyle
     * @reason Clamp the air and solid scans to the world height bounds and loaded blocks so pathing terminates on cube worlds.
     */
    @Nullable @Overwrite public Path getPathToPos(@Nonnull BlockPos posIn) {
        BlockPos posOriginal = posIn;
        if (world.getBlockState(posIn).getMaterial() == Material.AIR) {
            BlockPos pos = posIn.down();
            while (pos.getY() > ((IMinMaxHeight) world).rdpl$getMinHeight()
                    && world.isBlockLoaded(pos)
                    && world.getBlockState(pos).getMaterial() == Material.AIR) { pos = pos.down(); }
            if (pos.getY() > ((IMinMaxHeight) world).rdpl$getMinHeight() && world.isBlockLoaded(pos)) { return super.getPathToPos(pos.up()); }
            do {
                pos = pos.up();
            } while (pos.getY() < ((IMinMaxHeight) world).rdpl$getMaxHeight()
                    && world.isBlockLoaded(pos)
                    && world.getBlockState(pos).getMaterial() == Material.AIR);
            posIn = pos;
        }
        if (!world.getBlockState(posIn).getMaterial().isSolid()) { return super.getPathToPos(posIn); }
        else {
            BlockPos pos = posIn.up();
            while (pos.getY() < ((IMinMaxHeight) world).rdpl$getMaxHeight()
                    && world.isBlockLoaded(pos)
                    && this.world.getBlockState(pos).getMaterial().isSolid()) { pos = pos.up(); }
            if(pos.getY() >= ((IMinMaxHeight) world).rdpl$getMaxHeight() || !world.isBlockLoaded(pos)) { return super.getPathToPos(posOriginal); }
            return super.getPathToPos(pos);
        }
    }
}
