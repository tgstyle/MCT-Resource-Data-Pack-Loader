package mctmods.resourcedatapackloader.content.portal;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.block.ContentPortalBlock;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockMatchDef;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionPortalDef;
import mctmods.resourcedatapackloader.content.def.PortalFrameDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentPortals {
    private static final String PREFIX = "portal_";
    private static final List<Binding> BINDINGS = new ArrayList<>();
    private static boolean bound;

    private ContentPortals() {}

    public static ResourceLocation blockName(ResourceLocation dimension) { return ResourceLocation.fromNamespaceAndPath(dimension.getNamespace(), PREFIX + dimension.getPath()); }

    public static Map<ResourceLocation, DimensionDef> opening() {
        Map<ResourceLocation, DimensionDef> out = new LinkedHashMap<>();
        for (DimensionDef def : ContentDimensions.all()) {
            if (def.portal() != null) { out.put(def.key(), def); }
        }
        return out;
    }

    public static void prepare() {
        int made = 0;
        for (Map.Entry<ResourceLocation, DimensionDef> entry : opening().entrySet()) {
            DimensionDef dimension = entry.getValue();
            DimensionPortalDef portal = dimension.portal();
            if (portal == null || !ContentRegistry.available(dimension.requires(), dimension.key())) { continue; }
            ResourceLocation name = blockName(entry.getKey());
            BlockDef def = ContentParser.block(name, blockJson(entry.getKey(), portal));
            if (def == null) { continue; }
            ContentRegistry.addDef(def);
            made++;
        }
        if (made > 0) { ContentLog.LOGGER.info("{} dimension(s) bring a portal block of their own", made); }
    }

    private static String blockJson(ResourceLocation dimension, DimensionPortalDef portal) {
        return "{\"type\":\"portal\",\"material\":\"portal\",\"renderLayer\":\"translucent\",\"opaque\":false,\"fullCube\":false"
                + ",\"tint\":\"#" + String.format("%06X", portal.color() & 0xFFFFFF) + "\""
                + ",\"variants\":{\"" + PREFIX + dimension.getPath() + "\":{\"hardness\":-1,\"light\":11}}"
                + ",\"portal\":{\"dimension\":\"" + dimension + "\""
                + ",\"returnDimension\":\"" + portal.travel().returnDimension() + "\""
                + ",\"gate\":\"" + portal.travel().gate() + "\""
                + ",\"cooldown\":" + portal.travel().cooldown()
                + ",\"platform\":" + portal.travel().platform()
                + ",\"platformBlock\":\"" + portal.travel().platformBlock() + "\""
                + ",\"sound\":\"" + portal.travel().sound() + "\""
                + ",\"owned\":" + portal.travel().owned()
                + ",\"walkIn\":true}}";
    }

    private static void bind() {
        if (bound) { return; }
        bound = true;
        Set<String> claimed = new LinkedHashSet<>();
        for (Map.Entry<ResourceLocation, DimensionDef> entry : opening().entrySet()) {
            DimensionDef dimension = entry.getValue();
            DimensionPortalDef portal = dimension.portal();
            if (portal == null) { continue; }
            Item igniter = ContentStacks.item(ResourceLocation.tryParse(portal.ignitedBy()));
            if (igniter == null) {
                ContentLog.LOGGER.error("Dimension {} is lit by {}, which no mod registers, so nothing can open it", entry.getKey(), portal.ignitedBy());
                continue;
            }
            ContentRegistry.BlockEntry held = ContentRegistry.block(blockName(entry.getKey()));
            if (held == null || !(held.block() instanceof ContentPortalBlock block)) {
                ContentLog.LOGGER.error("Dimension {} has no portal block of its own, so it was never registered and cannot be opened", entry.getKey());
                continue;
            }
            List<PortalFrameDef> frames = new ArrayList<>();
            for (String name : portal.frames()) {
                PortalFrameDef frame = ContentPortalFrames.byName(name);
                if (frame == null) {
                    ContentLog.LOGGER.error("Dimension {} asks for portal frame {}, which no pack provides", entry.getKey(), name);
                    continue;
                }
                String mark = frame.key() + " lit by " + portal.ignitedBy();
                if (!claimed.add(mark)) {
                    ContentLog.LOGGER.error("Portal frame {} lit by {} is already claimed by another dimension, so {} leaves it alone", frame.key(), portal.ignitedBy(), entry.getKey());
                    continue;
                }
                frames.add(frame);
            }
            if (frames.isEmpty()) { continue; }
            BINDINGS.add(new Binding(entry.getKey(), dimension, portal, igniter, frames, block));
        }
        if (!BINDINGS.isEmpty()) { ContentLog.LOGGER.info("{} dimension(s) can be opened by building a frame and lighting it", BINDINGS.size()); }
    }

    public static List<Binding> bindings() {
        bind();
        return BINDINGS;
    }

    @Nullable public static Binding forBlock(Block block) {
        for (Binding binding : bindings()) {
            if (binding.block() == block) { return binding; }
        }
        return null;
    }

    @Nullable public static Lighting find(Level level, BlockPos clicked, @Nullable Direction face, ItemStack held) {
        if (held.isEmpty()) { return null; }
        for (Binding binding : bindings()) {
            if (held.getItem() != binding.igniter()) { continue; }
            if (level.dimension().location().equals(binding.name()) && !binding.portal().lightsBack()) { continue; }
            for (PortalFrameDef frame : binding.frames()) {
                for (BlockPos candidate : candidates(level, clicked, face)) {
                    PortalFit fit = ContentPortalFrames.fit(level, candidate, frame);
                    if (fit != null) { return new Lighting(binding, fit, candidate); }
                }
            }
            if (!level.isClientSide()) { ContentLog.LOGGER.debug("{} was held against {} and nothing there answers a frame of {}", held.getItem(), clicked, binding.name()); }
        }
        return null;
    }

    public static boolean light(Level level, BlockPos clicked, @Nullable Direction face, ItemStack held) {
        if (level.isClientSide()) { return false; }
        Lighting found = find(level, clicked, face, held);
        if (found == null) { return false; }
        fill(level, found.fit(), found.binding());
        PortalFit fit = found.fit();
        ContentLog.LOGGER.debug("{} was lit at {} and opened {}: {} by {} {}, {} block(s) of portal in it", fit.frame().name(), found.at(), found.binding().name(), fit.columns(), fit.rows(), fit.flat() ? "lying flat" : fit.alongX() ? "standing along x" : "standing along z", fit.size());
        return true;
    }

    private static List<BlockPos> candidates(Level level, BlockPos clicked, @Nullable Direction face) {
        List<BlockPos> out = new ArrayList<>();
        if (face != null && level.isEmptyBlock(clicked.relative(face))) { out.add(clicked.relative(face)); }
        for (Direction side : Direction.values()) {
            BlockPos beside = clicked.relative(side);
            if (!out.contains(beside) && level.isEmptyBlock(beside)) { out.add(beside); }
        }
        return out;
    }

    public static void fill(Level level, PortalFit fit, Binding binding) {
        BlockState state = binding.block().oriented(fit);
        for (BlockPos hole : fit.holes()) { level.setBlock(hole, state, 2); }
    }

    @Nullable public static PortalFit fitAt(Level level, BlockPos pos, Binding binding) {
        for (PortalFrameDef frame : binding.frames()) {
            PortalFit found = ContentPortalFrames.fit(level, pos, frame);
            if (found != null) { return found; }
        }
        return null;
    }

    public static void shaken(Level level, BlockPos pos, BlockState broken) {
        if (level.isClientSide() || !frameBlock(broken)) { return; }
        int reach = 0;
        for (Binding binding : bindings()) {
            for (PortalFrameDef frame : binding.frames()) { reach = Math.max(reach, Math.max(frame.rows().size(), Math.max(frame.maxWidth(), frame.maxHeight()))); }
        }
        if (reach == 0) { return; }
        List<BlockPos> going = new ArrayList<>();
        for (BlockPos at : BlockPos.betweenClosed(pos.offset(-reach, -reach, -reach), pos.offset(reach, reach, reach))) {
            if (!level.isLoaded(at)) { continue; }
            Binding binding = forBlock(level.getBlockState(at).getBlock());
            if (binding == null || standing(level, at, binding)) { continue; }
            going.add(at.immutable());
        }
        for (BlockPos at : going) { level.removeBlock(at, false); }
        if (!going.isEmpty()) { ContentLog.LOGGER.debug("A frame block broken at {} left {} portal block(s) with nothing holding them, so they went out", pos, going.size()); }
    }

    private static boolean frameBlock(BlockState broken) {
        for (Binding binding : bindings()) {
            for (PortalFrameDef frame : binding.frames()) {
                for (BlockMatchDef wanted : frame.legend().values()) {
                    if (ContentPortalFrames.matches(broken, wanted)) { return true; }
                }
            }
        }
        return false;
    }

    public static boolean standing(Level level, BlockPos pos, Binding binding) { return fitAt(level, pos, binding) != null; }

    public record Lighting(Binding binding, PortalFit fit, BlockPos at) {}

    public record Binding(ResourceLocation name, DimensionDef dimension, DimensionPortalDef portal, Item igniter, List<PortalFrameDef> frames, ContentPortalBlock block) {}
}
