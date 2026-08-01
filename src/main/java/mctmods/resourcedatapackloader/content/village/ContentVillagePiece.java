package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentVillagePiece extends StructureVillagePieces.Village {
    private String plot = "";

    @SuppressWarnings("unused") public ContentVillagePiece() {}

    public ContentVillagePiece(StructureVillagePieces.Start start, int type, StructureBoundingBox box, EnumFacing facing, VillageDef def) {
        super(start, type);
        setCoordBaseMode(facing);
        this.boundingBox = box;
        this.plot = def.registryName.toString();
    }

    @Override protected void writeStructureToNBT(@Nonnull NBTTagCompound tag) {
        super.writeStructureToNBT(tag);
        tag.setString("rdpl_plot", plot);
    }

    @Override protected void readStructureFromNBT(@Nonnull NBTTagCompound tag, @Nonnull TemplateManager templates) {
        super.readStructureFromNBT(tag, templates);
        plot = tag.getString("rdpl_plot");
    }

    @Override public boolean addComponentParts(@Nonnull World world, @Nonnull Random random, @Nonnull StructureBoundingBox box) {
        VillageDef def = ContentVillages.byName(plot);
        if (def == null) { return true; }

        if (averageGroundLvl < 0) {
            averageGroundLvl = getAverageGroundLevel(world, box);
            if (averageGroundLvl < 0) { return true; }

            boundingBox.offset(0, averageGroundLvl - boundingBox.maxY + def.height - 1, 0);
        }

        if (def.isTemplate()) { template(world, def, box); }
        else { farm(world, random, def, box); }

        if (def.villagers > 0) { residents(world, def, box); }

        return true;
    }

    private void residents(World world, VillageDef def, StructureBoundingBox box) {
        if (def.villagerEntity.isEmpty()) {
            spawnVillagers(world, box, def.villagerX, def.villagerY, def.villagerZ, def.villagers);
            return;
        }

        ResourceLocation name = new ResourceLocation(def.villagerEntity);
        for (int index = 0; index < def.villagers; index++) {
            int x = getXWithOffset(def.villagerX + index, def.villagerZ);
            int y = getYWithOffset(def.villagerY);
            int z = getZWithOffset(def.villagerX + index, def.villagerZ);
            if (!box.isVecInside(new BlockPos(x, y, z))) { continue; }

            Entity made = EntityList.createEntityByIDFromName(name, world);
            if (made == null) {
                ContentLog.LOGGER.error("Village plot {} wants {} to live in it, which nothing registers", def.registryName, name);
                return;
            }
            made.setLocationAndAngles(x + 0.5D, y, z + 0.5D, 0.0F, 0.0F);
            if (made instanceof EntityLiving) { ((EntityLiving) made).onInitialSpawn(world.getDifficultyForLocation(new BlockPos(made)), null); }
            world.spawnEntity(made);
        }
    }

    private void farm(World world, Random random, VillageDef def, StructureBoundingBox box) {
        IBlockState edge = state(def.edge, Blocks.LOG.getDefaultState());
        IBlockState soil = state(def.soil, Blocks.FARMLAND.getDefaultState());
        int lastX = def.width - 1;
        int lastZ = def.depth - 1;

        fillWithBlocks(world, box, 0, 1, 0, lastX, def.height, lastZ, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        fillWithBlocks(world, box, 0, 0, 0, 0, 0, lastZ, edge, edge, false);
        fillWithBlocks(world, box, lastX, 0, 0, lastX, 0, lastZ, edge, edge, false);
        fillWithBlocks(world, box, 1, 0, 0, lastX - 1, 0, 0, edge, edge, false);
        fillWithBlocks(world, box, 1, 0, lastZ, lastX - 1, 0, lastZ, edge, edge, false);

        int row = Math.max(1, def.rowWidth);
        int step = def.water ? row + 1 : row;
        for (int x = 1; x < lastX; x++) {
            boolean channel = def.water && (x - 1) % step == row;
            if (channel) {
                fillWithBlocks(world, box, x, 0, 1, x, 0, lastZ - 1, Blocks.WATER.getDefaultState(), Blocks.WATER.getDefaultState(), false);
                continue;
            }

            fillWithBlocks(world, box, x, 0, 1, x, 0, lastZ - 1, soil, soil, false);
            for (int z = 1; z < lastZ; z++) { setBlockState(world, crop(random, def), x, 1, z, box); }
        }

        for (int z = 0; z <= lastZ; z++) {
            for (int x = 0; x <= lastX; x++) {
                clearCurrentPositionBlocksUpwards(world, x, def.height, z, box);
                replaceAirAndLiquidDownwards(world, state(def.ground, Blocks.DIRT.getDefaultState()), x, -1, z, box);
            }
        }
    }

    private void template(World world, VillageDef def, StructureBoundingBox box) {
        if (!(world instanceof WorldServer)) { return; }

        ResourceLocation name = new ResourceLocation(def.structure);
        TemplateManager templates = ((WorldServer) world).getStructureTemplateManager();
        Template template = templates.get(world.getMinecraftServer(), name);
        if (template == null) {
            ContentLog.LOGGER.error("Village plot {} asks for template {}, which no pack provides, leaving the ground as it is", def.registryName, name);
            return;
        }

        Rotation rotation = rotation();
        PlacementSettings settings = new PlacementSettings().setRotation(rotation).setBoundingBox(box).setIgnoreEntities(true).setIntegrity(def.integrity / 100.0F);
        template.addBlocksToWorld(world, corner(rotation), settings);

        for (int z = 0; z <= def.depth - 1; z++) {
            for (int x = 0; x <= def.width - 1; x++) { replaceAirAndLiquidDownwards(world, state(def.ground, Blocks.DIRT.getDefaultState()), x, -1, z, box); }
        }
    }

    private BlockPos corner(Rotation rotation) {
        if (rotation == Rotation.CLOCKWISE_90) { return new BlockPos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ); }
        if (rotation == Rotation.CLOCKWISE_180) { return new BlockPos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ); }
        if (rotation == Rotation.COUNTERCLOCKWISE_90) { return new BlockPos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ); }

        return new BlockPos(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
    }

    private Rotation rotation() {
        EnumFacing facing = getCoordBaseMode();
        if (facing == EnumFacing.WEST) { return Rotation.CLOCKWISE_90; }
        if (facing == EnumFacing.NORTH) { return Rotation.CLOCKWISE_180; }
        if (facing == EnumFacing.EAST) { return Rotation.COUNTERCLOCKWISE_90; }

        return Rotation.NONE;
    }

    private IBlockState crop(Random random, VillageDef def) {
        if (def.crops.isEmpty()) { return Blocks.WHEAT.getDefaultState(); }

        IBlockState chosen = state(def.crops.get(random.nextInt(def.crops.size())), Blocks.WHEAT.getDefaultState());
        Block block = chosen.getBlock();
        if (!(block instanceof BlockCrops)) { return chosen; }

        return ((BlockCrops) block).withAge(MathHelper.getInt(random, 0, ((BlockCrops) block).getMaxAge()));
    }

    private static IBlockState state(@Nullable String name, IBlockState fallback) {
        if (name == null || name.isEmpty()) { return fallback; }

        int split = name.lastIndexOf(':');
        String plain = name;
        int meta = -1;
        if (split > 0 && isNumber(name.substring(split + 1))) {
            plain = name.substring(0, split);
            meta = Integer.parseInt(name.substring(split + 1));
        }

        ResourceLocation location = new ResourceLocation(plain);
        Block block = ForgeRegistries.BLOCKS.containsKey(location) ? ForgeRegistries.BLOCKS.getValue(location) : null;
        if (block == null) {
            ContentLog.LOGGER.error("Village plot names block {}, which no mod registers, using {} instead", location, fallback.getBlock().getRegistryName());
            return fallback;
        }
        return meta < 0 ? block.getDefaultState() : ContentStates.of(block, meta);
    }

    private static boolean isNumber(String value) {
        if (value.isEmpty()) { return false; }

        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) { return false; }
        }
        return true;
    }
}
