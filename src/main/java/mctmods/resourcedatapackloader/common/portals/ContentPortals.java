package mctmods.resourcedatapackloader.common.portals;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.blocks.fluid.FluidTouch;
import mctmods.resourcedatapackloader.common.blocks.portal.ContentPortalBlock;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.DimensionPortalDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortalFrameDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.util.world.BlockFlags;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class ContentPortals {
    public static final int ALONG_X = 0;
    public static final int ALONG_Z = 1;
    public static final int FLAT = 2;
    private static final String PREFIX = "portal_";
    private static final Set<ResourceLocation> MADE = new LinkedHashSet<>();
    private static volatile List<Binding> bound = Collections.emptyList();
    private static volatile Set<Fluid> litFluids = Collections.emptySet();
    private static volatile Set<Block> frameBlocks = Collections.emptySet();

    private ContentPortals() {}

    public static ResourceLocation blockName(ResourceLocation dimension) { return new ResourceLocation(dimension.getNamespace(), PREFIX + dimension.getPath()); }

    public static Map<ResourceLocation, DimensionDef> opening() {
        Map<ResourceLocation, DimensionDef> out = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, DimensionDef> entry : ContentDimensions.available().entrySet()) {
            if (entry.getValue().portal != null) { out.put(entry.getKey(), entry.getValue()); }
        }
        return out;
    }

    public static String blockJson(ResourceLocation dimension, DimensionDef def) {
        DimensionPortalDef portal = def.portal;
        if (portal == null) { return ""; }
        JsonObject json = new JsonObject();
        json.addProperty("type", "portal");
        json.addProperty("material", "portal");
        json.addProperty("renderLayer", "translucent");
        json.addProperty("opaque", false);
        json.addProperty("fullCube", false);
        json.addProperty("lightOpacity", 0);
        json.addProperty("tint", "#" + String.format("%06X", portal.color & 0xFFFFFF));
        JsonObject variants = new JsonObject();
        variants.add(dimension.getPath(), variant(ALONG_X));
        variants.add(dimension.getPath() + "_z", variant(ALONG_Z));
        variants.add(dimension.getPath() + "_flat", variant(FLAT));
        json.add("variants", variants);
        json.add("portal", travel(def, portal));
        return json.toString();
    }

    private static JsonObject travel(DimensionDef def, DimensionPortalDef portal) {
        JsonObject travel = new JsonObject();
        travel.addProperty("dimension", def.id);
        travel.addProperty("returnDimension", portal.travel.returnDimension);
        travel.addProperty("gate", portal.travel.gate);
        travel.addProperty("cooldown", portal.travel.cooldown);
        travel.addProperty("platform", portal.travel.platform);
        travel.addProperty("platformBlock", portal.travel.platformBlock);
        travel.addProperty("sound", portal.travel.sound);
        travel.addProperty("owned", portal.travel.owned);
        travel.addProperty("walkIn", true);
        travel.addProperty("landing", portal.travel.sameLanding ? "same" : "surface");
        travel.addProperty("torches", portal.travel.torches);
        travel.addProperty("useRange", portal.travel.useRange);
        travel.addProperty("mounted", portal.travel.mounted);
        travel.addProperty("returnTo", portal.travel.returnToOrigin ? "origin" : "fixed");
        return travel;
    }

    private static JsonObject variant(int meta) {
        JsonObject variant = new JsonObject();
        variant.addProperty("meta", meta);
        variant.addProperty("hardness", -1);
        variant.addProperty("light", 11);
        return variant;
    }

    public static void made(ResourceLocation name) { MADE.add(name); }

    public static boolean isMade(ResourceLocation name) { return MADE.contains(name); }

    public static void bind() {
        List<Binding> made = new ArrayList<>();
        Set<String> claimed = new LinkedHashSet<>();
        for (Map.Entry<ResourceLocation, DimensionDef> entry : opening().entrySet()) {
            DimensionDef dimension = entry.getValue();
            DimensionPortalDef portal = dimension.portal;
            if (portal == null) { continue; }
            Item igniter = portal.ignitedBy.isEmpty() ? null : ForgeRegistries.ITEMS.getValue(new ResourceLocation(portal.ignitedBy));
            if (igniter == null && !portal.ignitedBy.isEmpty()) { ContentLog.LOGGER.error("Dimension {} is lit by {}, which no mod registers, so nothing can open it", entry.getKey(), portal.ignitedBy); }
            Fluid fluid = portal.ignitedByFluid.isEmpty() ? null : FluidTouch.named(portal.ignitedByFluid);
            if (fluid == null && !portal.ignitedByFluid.isEmpty()) { ContentLog.LOGGER.error("Dimension {} is lit by fluid {}, which is not a loaded fluid with a block, so lighting it that way is off", entry.getKey(), portal.ignitedByFluid); }
            if (igniter == null && fluid == null) { continue; }
            Block block = ForgeRegistries.BLOCKS.getValue(blockName(entry.getKey()));
            if (!(block instanceof ContentPortalBlock)) {
                ContentLog.LOGGER.error("Dimension {} has no portal block of its own, so it was never registered and cannot be opened", entry.getKey());
                continue;
            }
            List<PortalFrameDef> frames = new ArrayList<>();
            List<PortalFrameDef> itemFrames = new ArrayList<>();
            List<PortalFrameDef> fluidFrames = new ArrayList<>();
            for (String name : portal.frames) {
                PortalFrameDef frame = ContentPortalFrames.byName(name);
                if (frame == null) {
                    ContentLog.LOGGER.error("Dimension {} asks for portal frame {}, which no pack provides", entry.getKey(), name);
                    continue;
                }
                boolean byItem = igniter != null && claimed.add(frame.registryName + " lit by " + portal.ignitedBy);
                boolean byFluid = fluid != null && claimed.add(frame.registryName + " lit by fluid " + portal.ignitedByFluid);
                if (igniter != null && !byItem) { ContentLog.LOGGER.error("Portal frame {} lit by {} is already claimed by another dimension, so {} leaves it alone", frame.registryName, portal.ignitedBy, entry.getKey()); }
                if (fluid != null && !byFluid) { ContentLog.LOGGER.error("Portal frame {} lit by fluid {} is already claimed by another dimension, so {} leaves it alone", frame.registryName, portal.ignitedByFluid, entry.getKey()); }
                if (byItem) { itemFrames.add(frame); }
                if (byFluid) { fluidFrames.add(frame); }
                if (byItem || byFluid) { frames.add(frame); }
            }
            if (frames.isEmpty()) { continue; }
            made.add(new Binding(entry.getKey(), dimension, portal, igniter, fluid, frames, itemFrames, fluidFrames, (ContentPortalBlock) block));
        }
        if (!made.isEmpty()) { ContentLog.LOGGER.info("{} dimension(s) can be opened by building a frame and lighting it", made.size()); }
        bound = Collections.unmodifiableList(made);
        Set<Fluid> lit = new HashSet<>();
        Set<Block> walls = new HashSet<>();
        for (Binding binding : made) {
            if (binding.fluid == null) { continue; }
            lit.add(binding.fluid);
            for (PortalFrameDef frame : binding.fluidFrames) {
                for (IBlockState wanted : frame.legend.values()) { walls.add(wanted.getBlock()); }
            }
        }
        litFluids = lit;
        frameBlocks = walls;
    }

    public static List<Binding> bindings() { return bound; }

    @Nullable public static Binding forBlock(Block block) {
        for (Binding binding : bindings()) {
            if (binding.block == block) { return binding; }
        }
        return null;
    }

    @Nullable public static Lighting find(World world, BlockPos clicked, @Nullable EnumFacing face, ItemStack held) {
        if (held.isEmpty()) { return null; }
        for (Binding binding : bindings()) {
            if (binding.igniter == null || held.getItem() != binding.igniter) { continue; }
            if (binding.blockedIn(world)) { continue; }
            for (PortalFrameDef frame : binding.itemFrames) {
                for (BlockPos candidate : candidates(world, clicked, face)) {
                    PortalFit fit = ContentPortalFrames.fit(world, candidate, frame);
                    if (fit != null) { return new Lighting(binding, fit, candidate); }
                }
            }
            if (!world.isRemote) { ContentLog.LOGGER.debug("{} was held against {} and nothing there answers a frame of {}", held.getItem().getRegistryName(), clicked, binding.name); }
        }
        return null;
    }

    public static boolean fluidLit() { return !litFluids.isEmpty(); }

    public static void lightByFluid(World world, BlockPos at, IBlockState state) {
        Fluid fluid = FluidTouch.fluidOf(state.getBlock());
        if (fluid == null || !litFluids.contains(fluid) || !besideFrame(world, at)) { return; }
        for (Binding binding : bindings()) {
            if (binding.fluid != fluid) { continue; }
            if (binding.blockedIn(world)) { continue; }
            for (PortalFrameDef frame : binding.fluidFrames) {
                PortalFit fit = ContentPortalFrames.fit(world, at, frame, null, fluid);
                if (fit == null) { continue; }
                fill(world, fit, binding);
                ContentLog.LOGGER.debug("{} was lit by {} at {} and opened {}", fit.frame.name, binding.portal.ignitedByFluid, at, binding.name);
                return;
            }
        }
    }

    private static boolean besideFrame(World world, BlockPos at) {
        for (EnumFacing side : EnumFacing.VALUES) {
            BlockPos beside = at.offset(side);
            if (world.isBlockLoaded(beside) && frameBlocks.contains(world.getBlockState(beside).getBlock())) { return true; }
        }
        return false;
    }

    public static boolean lightFailed(World world, BlockPos clicked, @Nullable EnumFacing face, ItemStack held) {
        if (world.isRemote) { return true; }
        Lighting found = find(world, clicked, face, held);
        if (found == null) { return true; }
        fill(world, found.fit, found.binding);
        PortalFit fit = found.fit;
        ContentLog.LOGGER.debug("{} was lit at {} and opened {}: {} by {} {}, {} block(s) of portal in it", fit.frame.name, found.at, found.binding.name, fit.columns, fit.rows, fit.flat ? "lying flat" : fit.alongX ? "standing along x" : "standing along z", fit.size());
        return false;
    }

    private static List<BlockPos> candidates(World world, BlockPos clicked, @Nullable EnumFacing face) {
        List<BlockPos> out = new ArrayList<>();
        if (face != null && world.isAirBlock(clicked.offset(face))) { out.add(clicked.offset(face)); }
        for (EnumFacing side : EnumFacing.VALUES) {
            BlockPos beside = clicked.offset(side);
            if (!out.contains(beside) && world.isAirBlock(beside)) { out.add(beside); }
        }
        return out;
    }

    public static void fill(World world, PortalFit fit, Binding binding) {
        IBlockState state = ContentStates.of(binding.block, fit.flat ? FLAT : fit.alongX ? ALONG_X : ALONG_Z);
        for (BlockPos hole : fit.holes) { world.setBlockState(hole, state, BlockFlags.UPDATE_CLIENTS); }
    }

    @Nullable public static PortalFit fitAt(World world, BlockPos pos, Binding binding) { return fitAt(world, pos, binding, null); }

    @Nullable private static PortalFit fitAt(World world, BlockPos pos, Binding binding, @Nullable BlockPos broken) {
        for (PortalFrameDef frame : binding.frames) {
            PortalFit found = ContentPortalFrames.fit(world, pos, frame, broken);
            if (found != null) { return found; }
        }
        return null;
    }

    public static void shaken(World world, BlockPos pos, IBlockState broken) {
        if (world.isRemote || !frameBlock(broken)) { return; }
        int reach = 0;
        for (Binding binding : bindings()) {
            for (PortalFrameDef frame : binding.frames) { reach = Math.max(reach, Math.max(frame.rows.size(), Math.max(frame.maxWidth, frame.maxHeight))); }
        }
        if (reach == 0) { return; }
        List<BlockPos> going = new ArrayList<>();
        for (BlockPos at : BlockPos.getAllInBoxMutable(pos.add(-reach, -reach, -reach), pos.add(reach, reach, reach))) {
            if (!world.isBlockLoaded(at)) { continue; }
            IBlockState found = world.getBlockState(at);
            Binding binding = forBlock(found.getBlock());
            if (binding == null || fitAt(world, at, binding, pos) != null) { continue; }
            going.add(at.toImmutable());
        }
        for (BlockPos at : going) { world.setBlockToAir(at); }
        if (!going.isEmpty()) { ContentLog.LOGGER.debug("A frame block broken at {} left {} portal block(s) with nothing holding them, so they went out", pos, going.size()); }
    }

    private static boolean frameBlock(IBlockState broken) {
        for (Binding binding : bindings()) {
            for (PortalFrameDef frame : binding.frames) {
                for (IBlockState wanted : frame.legend.values()) {
                    if (PortalShapes.matches(broken, wanted)) { return true; }
                }
            }
        }
        return false;
    }

    public static boolean standing(World world, BlockPos pos, Binding binding) { return fitAt(world, pos, binding) != null; }

    public static final class Lighting {
        public final Binding binding;
        public final PortalFit fit;
        public final BlockPos at;

        private Lighting(Binding binding, PortalFit fit, BlockPos at) {
            this.binding = binding;
            this.fit = fit;
            this.at = at;
        }
    }

    public static final class Binding {
        public final ResourceLocation name;
        public final DimensionDef dimension;
        public final DimensionPortalDef portal;
        @Nullable public final Item igniter;
        @Nullable public final Fluid fluid;
        public final List<PortalFrameDef> frames;
        public final List<PortalFrameDef> itemFrames;
        public final List<PortalFrameDef> fluidFrames;
        public final ContentPortalBlock block;

        private Binding(ResourceLocation name, DimensionDef dimension, DimensionPortalDef portal, @Nullable Item igniter, @Nullable Fluid fluid, List<PortalFrameDef> frames, List<PortalFrameDef> itemFrames, List<PortalFrameDef> fluidFrames, ContentPortalBlock block) {
            this.name = name;
            this.dimension = dimension;
            this.portal = portal;
            this.igniter = igniter;
            this.fluid = fluid;
            this.frames = frames;
            this.itemFrames = itemFrames;
            this.fluidFrames = fluidFrames;
            this.block = block;
        }

        private boolean blockedIn(World world) { return world.provider.getDimension() == dimension.id && portal.noLightBack(); }
    }
}
