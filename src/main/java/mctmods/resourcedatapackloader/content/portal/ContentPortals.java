package mctmods.resourcedatapackloader.content.portal;

import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.block.ContentBlockPortal;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionPortalDef;
import mctmods.resourcedatapackloader.content.def.PortalFrameDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentPortals {
    public static final int ALONG_X = 0;
    public static final int ALONG_Z = 1;
    public static final int FLAT = 2;
    private static final String PREFIX = "portal_";
    private static final List<Binding> BINDINGS = new ArrayList<>();
    private static final Set<ResourceLocation> MADE = new LinkedHashSet<>();
    private static boolean bound;

    private ContentPortals() {}

    public static ResourceLocation blockName(ResourceLocation dimension) { return new ResourceLocation(dimension.getNamespace(), PREFIX + dimension.getPath()); }

    public static Map<ResourceLocation, DimensionDef> opening() {
        Map<ResourceLocation, DimensionDef> out = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, DimensionDef> entry : ContentDimensions.all().entrySet()) {
            if (entry.getValue().portal != null) { out.put(entry.getKey(), entry.getValue()); }
        }
        return out;
    }

    public static String blockJson(ResourceLocation dimension, DimensionDef def) {
        DimensionPortalDef portal = def.portal;
        if (portal == null) { return ""; }
        String plain = "{\"meta\":%d,\"hardness\":-1,\"light\":11}";
        return "{\"type\":\"portal\",\"material\":\"portal\",\"renderLayer\":\"translucent\",\"opaque\":false,\"fullCube\":false,\"lightOpacity\":0"
                + ",\"tint\":\"#" + String.format("%06X", portal.color & 0xFFFFFF) + "\""
                + ",\"variants\":{\"" + dimension.getPath() + "\":" + String.format(plain, ALONG_X)
                + ",\"" + dimension.getPath() + "_z\":" + String.format(plain, ALONG_Z)
                + ",\"" + dimension.getPath() + "_flat\":" + String.format(plain, FLAT) + "}"
                + ",\"portal\":{\"dimension\":" + def.id
                + ",\"returnDimension\":" + portal.travel.returnDimension
                + ",\"gate\":\"" + portal.travel.gate + "\""
                + ",\"cooldown\":" + portal.travel.cooldown
                + ",\"platform\":" + portal.travel.platform
                + ",\"platformBlock\":\"" + portal.travel.platformBlock + "\""
                + ",\"sound\":\"" + portal.travel.sound + "\""
                + ",\"owned\":" + portal.travel.owned
                + ",\"walkIn\":true}}";
    }

    public static void made(ResourceLocation name) { MADE.add(name); }

    public static boolean isMade(ResourceLocation name) { return MADE.contains(name); }

    private static void bind() {
        if (bound) { return; }
        bound = true;
        Set<String> claimed = new LinkedHashSet<>();
        for (Map.Entry<ResourceLocation, DimensionDef> entry : opening().entrySet()) {
            DimensionDef dimension = entry.getValue();
            DimensionPortalDef portal = dimension.portal;
            if (portal == null) { continue; }
            Item igniter = ForgeRegistries.ITEMS.getValue(new ResourceLocation(portal.ignitedBy));
            if (igniter == null) {
                ContentLog.LOGGER.error("Dimension {} is lit by {}, which no mod registers, so nothing can open it", entry.getKey(), portal.ignitedBy);
                continue;
            }
            Block block = ForgeRegistries.BLOCKS.getValue(blockName(entry.getKey()));
            if (!(block instanceof ContentBlockPortal)) {
                ContentLog.LOGGER.error("Dimension {} has no portal block of its own, so it was never registered and cannot be opened", entry.getKey());
                continue;
            }
            List<PortalFrameDef> frames = new ArrayList<>();
            for (String name : portal.frames) {
                PortalFrameDef frame = ContentPortalFrames.byName(name);
                if (frame == null) {
                    ContentLog.LOGGER.error("Dimension {} asks for portal frame {}, which no pack provides", entry.getKey(), name);
                    continue;
                }
                String mark = frame.registryName + " lit by " + portal.ignitedBy;
                if (!claimed.add(mark)) {
                    ContentLog.LOGGER.error("Portal frame {} lit by {} is already claimed by another dimension, so {} leaves it alone", frame.registryName, portal.ignitedBy, entry.getKey());
                    continue;
                }
                frames.add(frame);
            }
            if (frames.isEmpty()) { continue; }
            BINDINGS.add(new Binding(entry.getKey(), dimension, portal, igniter, frames, (ContentBlockPortal) block));
        }
        if (!BINDINGS.isEmpty()) { ContentLog.LOGGER.info("{} dimension(s) can be opened by building a frame and lighting it", BINDINGS.size()); }
    }

    public static List<Binding> bindings() {
        bind();
        return BINDINGS;
    }

    @Nullable public static Binding forBlock(Block block) {
        for (Binding binding : bindings()) {
            if (binding.block == block) { return binding; }
        }
        return null;
    }

    @Nullable public static Lighting find(World world, BlockPos clicked, @Nullable EnumFacing face, ItemStack held) {
        if (held.isEmpty()) { return null; }
        for (Binding binding : bindings()) {
            if (held.getItem() != binding.igniter) { continue; }
            if (world.provider.getDimension() == binding.dimension.id && !binding.portal.lightsBack()) { continue; }
            for (PortalFrameDef frame : binding.frames) {
                for (BlockPos candidate : candidates(world, clicked, face)) {
                    PortalFit fit = ContentPortalFrames.fit(world, candidate, frame);
                    if (fit != null) { return new Lighting(binding, fit, candidate); }
                }
            }
            if (!world.isRemote) { ContentLog.LOGGER.debug("{} was held against {} and nothing there answers a frame of {}", held.getItem().getRegistryName(), clicked, binding.name); }
        }
        return null;
    }

    public static boolean light(World world, BlockPos clicked, @Nullable EnumFacing face, ItemStack held) {
        if (world.isRemote) { return false; }
        Lighting found = find(world, clicked, face, held);
        if (found == null) { return false; }
        fill(world, found.fit, found.binding);
        PortalFit fit = found.fit;
        ContentLog.LOGGER.debug("{} was lit at {} and opened {}: {} by {} {}, {} block(s) of portal in it", fit.frame.name, found.at, found.binding.name, fit.columns, fit.rows, fit.flat ? "lying flat" : fit.alongX ? "standing along x" : "standing along z", fit.size());
        return true;
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
        for (BlockPos hole : fit.holes) { world.setBlockState(hole, state, 2); }
    }

    @Nullable public static PortalFit fitAt(World world, BlockPos pos, Binding binding) {
        for (PortalFrameDef frame : binding.frames) {
            PortalFit found = ContentPortalFrames.fit(world, pos, frame);
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
            if (binding == null || standing(world, at, binding)) { continue; }
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
        public final Item igniter;
        public final List<PortalFrameDef> frames;
        public final ContentBlockPortal block;

        private Binding(ResourceLocation name, DimensionDef dimension, DimensionPortalDef portal, Item igniter, List<PortalFrameDef> frames, ContentBlockPortal block) {
            this.name = name;
            this.dimension = dimension;
            this.portal = portal;
            this.igniter = igniter;
            this.frames = frames;
            this.block = block;
        }
    }
}
