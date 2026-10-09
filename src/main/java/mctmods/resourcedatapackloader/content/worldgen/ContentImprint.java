package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Turns;
import mctmods.resourcedatapackloader.util.world.BlockFlags;
import mctmods.resourcedatapackloader.util.world.StructureLoot;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.BlockRotationProcessor;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.common.util.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class ContentImprint implements IContentShape {
    private static final int WORLDGEN_FLAGS = BlockFlags.UPDATE_CLIENTS;
    private static final int OFFSET = 8;
    private static final Map<String, List<IBlockState>> PALETTES = new ConcurrentHashMap<>();
    private final ContentPlacer placer;
    private final ShapeDef shape;
    private final ResourceLocation key;
    private final boolean filtered;
    private final List<String> names = new ArrayList<>();

    public ContentImprint(ContentPlacer placer, ShapeDef shape, ResourceLocation key, boolean filtered) {
        this.placer = placer;
        this.shape = shape;
        if (shape.structures.isEmpty()) { names.add(shape.structure); }
        else {
            for (PickDef pick : shape.structures) { names.add(pick.name); }
        }
        this.key = key;
        this.filtered = filtered;
    }

    public static int backX(Rotation rotation, BlockPos span) { return rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.CLOCKWISE_180 ? span.getX() - 1 : 0; }

    public static int backZ(Rotation rotation, BlockPos span) { return rotation == Rotation.CLOCKWISE_180 || rotation == Rotation.COUNTERCLOCKWISE_90 ? span.getZ() - 1 : 0; }

    public int[] pinnedAt() { return shape.at != null && shape.at.length == 2 ? shape.at : null; }

    @Override public boolean places(World world, IBlockState wood, List<IBlockState> canopies) { return places(world, wood, canopies, null); }

    @Override public boolean places(World world, IBlockState wood, List<IBlockState> canopies, @Nullable String structure) {
        if (!(world instanceof WorldServer)) { return false; }
        if (structure != null) { return offers(world, structure, wood, canopies); }
        for (String named : names) {
            if (offers(world, named, wood, canopies)) { return true; }
        }
        return false;
    }

    private boolean offers(World world, @Nullable String named, IBlockState wood, List<IBlockState> canopies) {
        if (named == null || named.isEmpty()) { return false; }
        List<IBlockState> states = PALETTES.get(named);
        if (states == null) {
            Template loaded = template(world, named);
            if (loaded == null) { return false; }
            states = new ArrayList<>();
            NBTTagList palette = loaded.writeToNBT(new NBTTagCompound()).getTagList("palette", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < palette.tagCount(); i++) { states.add(NBTUtil.readBlockState(palette.getCompoundTagAt(i))); }
            PALETTES.put(named, states);
        }
        return holds(states, wood, canopies);
    }

    @Nullable private static Template template(World world, String named) {
        if (!(world instanceof WorldServer)) { return null; }
        WorldServer server = (WorldServer) world;
        return server.getStructureTemplateManager().get(server.getMinecraftServer(), new ResourceLocation(named));
    }

    @Override public int reach(World world) { return span(world) + 16; }

    public int span(World world) {
        int widest = 0;
        for (String named : names) {
            if (named == null || named.isEmpty()) { continue; }
            Template loaded = template(world, named);
            if (loaded != null) { widest = Math.max(widest, Math.max(loaded.getSize().getX(), loaded.getSize().getZ())); }
        }
        return widest;
    }

    @Nullable @Override public Footprint footprint(World world, Random random, int x, int z, Predicate<Footprint> refused) {
        String named = PickDef.pick(shape.structures, random, shape.structure);
        if (named == null || named.isEmpty()) { return null; }
        Template loaded = template(world, named);
        if (loaded == null) { return null; }
        Rotation rotation = turn(random);
        mirror(random);
        BlockPos span = loaded.transformedSize(rotation);
        int cornerX = within(x, x - span.getX() / 2, span.getX());
        int cornerZ = within(z, z - span.getZ() / 2, span.getZ());
        Footprint box = new Footprint(cornerX, cornerZ, cornerX + span.getX() - 1, cornerZ + span.getZ() - 1, named);
        return refused.test(box) ? null : box;
    }

    private static boolean holds(List<IBlockState> palette, IBlockState wood, List<IBlockState> canopies) {
        boolean timber = false;
        boolean crown = canopies.isEmpty();
        for (IBlockState state : palette) {
            if (ContentStates.alike(state, wood)) { timber = true; }
            for (IBlockState canopy : canopies) {
                if (ContentStates.alike(state, canopy)) { crown = true; }
            }
        }
        return timber && crown;
    }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        String named = PickDef.pick(shape.structures, random, shape.structure);
        if (named == null || named.isEmpty() || !(world instanceof WorldServer)) { return false; }
        Template loaded = template(world, named);
        if (loaded == null) {
            ContentLog.LOGGER.error("Worldgen {} places structure '{}', which could not be loaded, so nothing generates", key, named);
            return false;
        }
        Rotation rotation = turn(random);
        PlacementSettings settings = new PlacementSettings();
        settings.setRotation(rotation);
        Mirror mirror = mirror(random);
        settings.setMirror(mirror);
        settings.setIntegrity(shape.integrity / (float) ShapeDef.FULL_INTEGRITY);
        settings.setRandom(random);
        BlockPos span = loaded.transformedSize(rotation);
        int cornerX = within(origin.getX(), origin.getX() - span.getX() / 2, span.getX());
        int cornerZ = within(origin.getZ(), origin.getZ() - span.getZ() / 2, span.getZ());
        if (ContentPlacer.keepsAny(cornerX, cornerZ, cornerX + span.getX() - 1, cornerZ + span.getZ() - 1)) { return false; }
        BlockPos fitted = loaded.getZeroPositionWithTransform(new BlockPos(cornerX, origin.getY(), cornerZ), mirror, rotation);
        if (span.getX() > 16 || span.getZ() > 16) {
            int reach = Math.max(span.getX(), span.getZ());
            if (!ContentPlacer.areaLoaded(world, fitted.getX() - reach, fitted.getZ() - reach, fitted.getX() + reach, fitted.getZ() + reach)) { return false; }
        }
        if (filtered) { loaded.addBlocksToWorld(world, fitted, onlyOverReplaceable(new BlockRotationProcessor(fitted, settings)), settings, WORLDGEN_FLAGS); }
        else { loaded.addBlocksToWorld(world, fitted, settings, WORLDGEN_FLAGS); }
        StructureLoot.stock(world, cornerX, origin.getY(), cornerZ, cornerX + span.getX() - 1, origin.getY() + span.getY() - 1, cornerZ + span.getZ() - 1, shape.lootTable, random);
        if (shape.locateAs != null && !shape.locateAs.isEmpty()) { ContentLocate.record(world, shape.locateAs, fitted); }
        return true;
    }

    private ITemplateProcessor onlyOverReplaceable(BlockRotationProcessor rotating) { return (holder, at, info) -> { Template.BlockInfo processed = rotating.processBlock(holder, at, info); return placer.occupied(holder, at.getX(), at.getY(), at.getZ()) ? null : processed; }; }

    private Rotation turn(Random random) {
        String named = PickDef.pick(shape.turns, random, "");
        if (ShapeDef.NO_TURN.equals(named)) { return Rotation.NONE; }
        if (ShapeDef.QUARTER.equals(named)) { return Rotation.CLOCKWISE_90; }
        if (ShapeDef.HALF.equals(named)) { return Rotation.CLOCKWISE_180; }
        if (ShapeDef.THREEQUARTER.equals(named)) { return Rotation.COUNTERCLOCKWISE_90; }
        return Turns.ALL[random.nextInt(Turns.ALL.length)];
    }

    private Mirror mirror(Random random) {
        String named = PickDef.pick(shape.mirrors, random, "");
        if (ShapeDef.LEFTRIGHT.equals(named)) { return Mirror.LEFT_RIGHT; }
        if (ShapeDef.FRONTBACK.equals(named)) { return Mirror.FRONT_BACK; }
        return Mirror.NONE;
    }

    private static int within(int origin, int start, int span) {
        if (span >= 16) { return start; }
        int corner = ((origin - OFFSET) >> 4) * 16 + OFFSET;
        return MathHelper.clamp(start, corner, corner + 16 - span);
    }
}
