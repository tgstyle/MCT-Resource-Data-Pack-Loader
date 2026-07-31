package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;
import mctmods.resourcedatapackloader.content.types.ContentTypes;

import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentBlockTorch extends BlockTorch implements IContentBlock {
    public static final int MAX_VARIANTS = 1;
    private static final double MIN_RED = 1.0 / 255.0;
    private final BlockDef def;

    public ContentBlockTorch(BlockDef def) {
        this.def = def;
        BlockVariant variant = def.at(0);

        setRegistryName(def.registryName);
        setTranslationKey(def.registryName + "." + variant.name);
        ContentSetup.harvest(this, def);
        if (def.soundType != null) { setSoundType(def.soundType); }
        setHardness(variant.hardness);
        setLightLevel(variant.light / 15.0F);
        ContentSetup.apply(this, def.creativeTab);
        ContentSetup.properties(this, def);
    }

    @Override public BlockDef getDef() { return def; }

    @Override @Nullable public ItemBlock createItem() { return new ItemBlock(this); }

    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def.renderLayer; }

    @Override @SideOnly(Side.CLIENT)
    public void randomDisplayTick(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
        if (Objects.equals(def.torchParticle, BlockDef.PARTICLE_NONE)) { return; }

        EnumFacing facing = state.getValue(FACING);
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.7;
        double z = pos.getZ() + 0.5;

        if (facing.getAxis().isHorizontal()) {
            EnumFacing opposite = facing.getOpposite();
            x += 0.27 * opposite.getXOffset();
            y += 0.22;
            z += 0.27 * opposite.getZOffset();
        }

        if (def.torchSmoke) { world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0, 0.0, 0.0); }

        if (Objects.equals(def.torchParticle, BlockDef.PARTICLE_COLOURED)) {
            world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z,
                    Math.max(MIN_RED, ContentTypes.red(def.torchColour)), ContentTypes.green(def.torchColour), ContentTypes.blue(def.torchColour));
            return;
        }
        world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0);
    }

}
