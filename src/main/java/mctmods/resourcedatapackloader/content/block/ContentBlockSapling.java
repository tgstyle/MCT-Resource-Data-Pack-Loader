package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.def.SaplingDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;
import mctmods.resourcedatapackloader.content.worldgen.ContentTreeGenerator;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentBlockSapling extends BlockBush implements IGrowable, IContentBlock {
    public static final int MAX_VARIANTS = 1;
    private static final int WORLDGEN_FLAGS = 16;
    private static final ThreadLocal<PropertyInteger> PENDING = new ThreadLocal<>();
    private final BlockDef def;
    private final SaplingDef sapling;
    private Set<Block> soil = new HashSet<>();
    private final PropertyInteger stage;

    public static ContentBlockSapling create(BlockDef def, SaplingDef sapling) {
        PENDING.set(PropertyInteger.create("stage", 0, Math.max(1, sapling.stages) - 1));
        try { return new ContentBlockSapling(def, sapling, PENDING.get()); }
        finally { PENDING.remove(); }
    }

    protected ContentBlockSapling(BlockDef def, SaplingDef sapling, PropertyInteger stage) {
        super(def.material);
        this.def = def;
        this.sapling = sapling;
        this.stage = stage;
        BlockVariant variant = def.at(0);
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName + "." + variant.name);
        if (def.soundType != null) { setSoundType(def.soundType); }
        setHardness(variant.hardness);
        setTickRandomly(true);
        ContentSetup.apply(this, def.creativeTab);
        ContentSetup.properties(this, def);
        setDefaultState(this.blockState.getBaseState().withProperty(stage, 0));
    }

    @Override @Nonnull protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, PENDING.get()); }

    @Override public BlockDef getDef() { return def; }

    @Override @Nullable public ItemBlock createItem() { return new ItemBlock(this); }

    @Override @Nonnull public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(stage, Math.max(0, Math.min(stage.getAllowedValues().size() - 1, meta)));
    }

    @Override public int getMetaFromState(IBlockState state) { return state.getValue(stage); }

    public void resolveSoil() {
        Set<Block> resolved = new HashSet<>();
        for (String name : sapling.soil) {
            ResourceLocation key = new ResourceLocation(name);
            Block block = ForgeRegistries.BLOCKS.containsKey(key) ? ForgeRegistries.BLOCKS.getValue(key) : null;
            if (block != null) { resolved.add(block); }
            else { ContentLog.LOGGER.error("Sapling {} names soil {}, which is not registered, leaving it out", def.registryName, name); }
        }
        this.soil = resolved;
    }

    @Override protected boolean canSustainBush(@Nonnull IBlockState state) {
        if (soil.isEmpty()) { return super.canSustainBush(state); }
        return soil.contains(state.getBlock());
    }

    @Override public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand) {
        if (world.isRemote) { return; }
        super.updateTick(world, pos, state, rand);
        if (!world.isAreaLoaded(pos, 1)) { return; }
        if (world.getLightFromNeighbors(pos.up()) < sapling.light) { return; }
        if (rand.nextInt(Math.max(1, sapling.chance)) != 0) { return; }
        advance(world, pos, state, rand);
    }

    @Override public boolean canGrow(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, boolean isClient) { return true; }

    @Override public boolean canUseBonemeal(@Nonnull World world, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull IBlockState state) { return true; }

    @Override public void grow(@Nonnull World world, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull IBlockState state) { advance(world, pos, state, rand); }

    private void advance(World world, BlockPos pos, IBlockState state, Random rand) {
        int current = state.getValue(stage);
        if (current < stage.getAllowedValues().size() - 1) {
            world.setBlockState(pos, state.withProperty(stage, current + 1), 4);
            return;
        }
        generate(world, pos, rand);
    }

    private void generate(World world, BlockPos pos, Random rand) {
        if (sapling.usesStructure()) {
            placeStructure(world, pos);
            return;
        }
        IBlockState log = ContentStates.parse(sapling.log, def.registryName);
        IBlockState leaves = ContentStates.parse(sapling.leaves, def.registryName);
        if (log == null || leaves == null) { return; }
        world.setBlockToAir(pos);
        ContentTreeGenerator tree = new ContentTreeGenerator(true, Math.max(1, sapling.height), log, leaves, soil);
        if (!tree.generate(world, rand, pos)) { world.setBlockState(pos, getDefaultState(), 4); }
    }

    private void placeStructure(World world, BlockPos pos) {
        if (!(world instanceof WorldServer)) { return; }
        WorldServer server = (WorldServer) world;
        MinecraftServer host = server.getMinecraftServer();
        Template template = server.getStructureTemplateManager().get(host, new ResourceLocation(sapling.structure));
        if (template == null) {
            ContentLog.LOGGER.error("Sapling {} grows into structure '{}', which could not be loaded, so it stays a sapling", def.registryName, sapling.structure);
            return;
        }
        BlockPos size = template.getSize();
        BlockPos origin = pos.add(-(size.getX() / 2), 0, -(size.getZ() / 2));
        world.setBlockToAir(pos);
        template.addBlocksToWorld(world, origin, new PlacementSettings(), WORLDGEN_FLAGS);
    }

    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def.renderLayer; }

    @Override public boolean isOpaqueCube(@Nonnull IBlockState state) { return false; }

    @Override public boolean isFullCube(@Nonnull IBlockState state) { return false; }
}
