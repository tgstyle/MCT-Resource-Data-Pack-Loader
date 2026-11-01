package mctmods.resourcedatapackloader.common.blocks.shape;

import mctmods.resourcedatapackloader.common.blocks.ContentSetup;
import mctmods.resourcedatapackloader.common.blocks.IContentBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockVariant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation") public class ContentLadderBlock extends BlockLadder implements IContentBlock {
    public static final int MAX_VARIANTS = 1;
    private final BlockDef def;

    public ContentLadderBlock(BlockDef def) {
        this.def = def;
        BlockVariant variant = def.at(0);
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName + "." + variant.name);
        ContentSetup.harvest(this, def);
        if (def.soundType != null) { setSoundType(def.soundType); }
        ContentSetup.apply(this, def.creativeTab);
        ContentSetup.properties(this, def);
    }

    @Override public BlockDef getDef() { return def; }

    @Override @Nullable public ItemBlock createItem() { return new ItemBlock(this); }

    @Override public float getExplosionResistance(@Nonnull World world, @Nonnull BlockPos pos, Entity exploder, @Nonnull Explosion explosion) { return ContentSetup.resistanceOf(def, def.at(0)); }

    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def.renderLayer; }

    @Override public boolean isOpaqueCube(@Nonnull IBlockState state) { return false; }
}
