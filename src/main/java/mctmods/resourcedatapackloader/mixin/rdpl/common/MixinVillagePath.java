package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.village.CityGrowth;
import mctmods.resourcedatapackloader.content.village.CityLayout;
import mctmods.resourcedatapackloader.content.village.ContentVillages;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IRoadLayout;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IVillageBlock;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.GroundLevel;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.TemplateManager;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import javax.annotation.Nullable;

@Mixin(StructureVillagePieces.Path.class) public abstract class MixinVillagePath extends StructureVillagePieces.Road implements IRoadLayout {
    @Inject(method = "findPieceBox", at = @At("RETURN"), cancellable = true) private static void rdpl$backOff(StructureVillagePieces.Start start, List<StructureComponent> p_175848_1_, Random rand, int p_175848_3_, int p_175848_4_, int p_175848_5_, EnumFacing facing, CallbackInfoReturnable<StructureBoundingBox> cir) {
        StructureBoundingBox box = cir.getReturnValue();
        if (box == null || facing == null || !ContentBeard.wanted()) { return; }
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        int rows = (alongX ? box.maxX - box.minX : box.maxZ - box.minZ) + 1;
        List<StructureComponent> held = ContentBeard.laid();
        int kept;
        ContentBeard.laying(p_175848_1_);
        try { kept = BeardRoads.roadReach(box, facing); }
        finally { ContentBeard.laying(held); }
        int room = ContentBeard.roomFor(p_175848_1_, box, facing);
        if (room < kept) {
            ContentLog.LOGGER.debug("A road from {}, {} facing {} runs into another village's piece after {} row(s), so it stops short of it", p_175848_3_, p_175848_5_, facing, room);
            kept = room;
        }
        if (kept >= rows) { return; }
        if (kept < 7) {
            ContentLog.LOGGER.debug("A road from {}, {} facing {} cannot be graded to a walkable slope or has no room beside another village, so it is not laid", p_175848_3_, p_175848_5_, facing);
            cir.setReturnValue(null);
            return;
        }
        int step = (alongX ? facing.getXOffset() : facing.getZOffset()) >= 0 ? 1 : -1;
        if (alongX && step > 0) { box.maxX = box.minX + kept - 1; }
        else if (alongX) { box.minX = box.maxX - kept + 1; }
        else if (step > 0) { box.maxZ = box.minZ + kept - 1; }
        else { box.minZ = box.maxZ - kept + 1; }
        ContentLog.LOGGER.debug("A road from {}, {} facing {} backs off from {} to {} block(s) to keep a walkable slope", p_175848_3_, p_175848_5_, facing, rows, kept);
    }

    @Unique private BeardRoads.Grade rdpl$stored;

    @Inject(method = "<init>(Lnet/minecraft/world/gen/structure/StructureVillagePieces$Start;ILjava/util/Random;Lnet/minecraft/world/gen/structure/StructureBoundingBox;Lnet/minecraft/util/EnumFacing;)V", at = @At("TAIL"))
    private void rdpl$inheritBlock(StructureVillagePieces.Start start, int p_i45562_2_, Random rand, StructureBoundingBox p_i45562_4_, EnumFacing facing, CallbackInfo ci) {
        if (start instanceof IVillageBlock) { ((IVillageBlock) this).rdpl$block(((IVillageBlock) start).rdpl$block()); }
    }

    @Redirect(method = "findPieceBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;getInt(Ljava/util/Random;II)I"))
    private static int rdpl$longerRuns(Random random, int minimum, int maximum, StructureVillagePieces.Start start, List<StructureComponent> p_175848_1_, Random rand, int p_175848_3_, int p_175848_4_, int p_175848_5_, EnumFacing facing) {
        if (!ContentBeard.wanted()) { return MathHelper.getInt(random, minimum, maximum); }
        int least = Math.max(5, (ContentVillages.largestPlot() + 9) / 7);
        int block = start instanceof IVillageBlock ? ((IVillageBlock) start).rdpl$block() : 0;
        if (block > 0 && !rdpl$fromWell(p_175848_1_, p_175848_3_ - facing.getXOffset(), p_175848_5_ - facing.getZOffset())) { least = Math.max(5, Math.min(least, (2 * block + BeardRoads.pathFullWidth() + 6) / 7)); }
        int rolled = MathHelper.getInt(random, least, least + 2);
        ContentLog.LOGGER.debug("A road rolls {} segments of 7, {} blocks", rolled, rolled * 7);
        return rolled;
    }

    @Unique private static boolean rdpl$fromWell(List<StructureComponent> own, int backX, int backZ) {
        for (StructureComponent other : own) {
            if (!(other instanceof StructureVillagePieces.Well)) { continue; }
            StructureBoundingBox well = other.getBoundingBox();
            if (backX >= well.minX && backX <= well.maxX && backZ >= well.minZ && backZ <= well.maxZ) { return true; }
        }
        return false;
    }

    @Redirect(method = "findPieceBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/structure/StructureComponent;findIntersecting(Ljava/util/List;Lnet/minecraft/world/gen/structure/StructureBoundingBox;)Lnet/minecraft/world/gen/structure/StructureComponent;"))
    @SuppressWarnings("ConstantConditions") private static StructureComponent rdpl$whoBlocks(List<StructureComponent> listIn, StructureBoundingBox boundingboxIn) {
        StructureComponent blocker = StructureComponent.findIntersecting(listIn, boundingboxIn);
        if (blocker != null) { ContentLog.LOGGER.debug("A road attempt {} is blocked by {} at {}", boundingboxIn, blocker.getClass().getSimpleName(), blocker.getBoundingBox()); }
        return blocker;
    }

    @Inject(method = "findPieceBox", at = @At("RETURN"), cancellable = true) private static void rdpl$widen(StructureVillagePieces.Start start, List<StructureComponent> p_175848_1_, Random rand, int p_175848_3_, int p_175848_4_, int p_175848_5_, EnumFacing facing, CallbackInfoReturnable<StructureBoundingBox> cir) {
        StructureBoundingBox found = cir.getReturnValue();
        ContentLog.LOGGER.debug("A road box comes back {} facing {}: {}", found == null ? "null" : (Math.max(found.maxX - found.minX, found.maxZ - found.minZ) + 1) + " long", facing, found);
        if (found == null || !ContentBeard.wanted()) { return; }
        if (rdpl$acrossPlaza(p_175848_1_, found) || ContentBeard.taken(p_175848_1_, found)) {
            cir.setReturnValue(null);
            return;
        }
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        int half = (BeardRoads.pathFullWidth() - 3) / 2;
        if (!rdpl$lineUp(p_175848_1_, found, alongX, half)) {
            cir.setReturnValue(null);
            return;
        }
        if (half > 0) {
            StructureBoundingBox wide = new StructureBoundingBox(found);
            if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
                wide.minX -= half;
                wide.maxX += half;
            }
            else {
                wide.minZ -= half;
                wide.maxZ += half;
            }
            boolean rolled = BeardRoads.alleyChance() > 0 && rand.nextInt(100) < BeardRoads.alleyChance();
            int rows = (alongX ? wide.maxX - wide.minX : wide.maxZ - wide.minZ) + 1;
            int room = ContentBeard.roomFor(p_175848_1_, wide, facing);
            if (room < rows && room >= 7) {
                int step = (alongX ? facing.getXOffset() : facing.getZOffset()) >= 0 ? 1 : -1;
                for (StructureBoundingBox box : new StructureBoundingBox[] { wide, found }) {
                    if (alongX && step > 0) { box.maxX = box.minX + room - 1; }
                    else if (alongX) { box.minX = box.maxX - room + 1; }
                    else if (step > 0) { box.maxZ = box.minZ + room - 1; }
                    else { box.minZ = box.maxZ - room + 1; }
                }
                ContentLog.LOGGER.debug("A road attempt {} facing {} widened would run into another village's piece after {} row(s), so it stops short of it", wide, facing, room);
            }
            if (!rdpl$tooNear(start, p_175848_1_, wide, facing) && rdpl$widensPast(p_175848_1_, wide, facing) && !ContentBeard.taken(p_175848_1_, wide)) {
                if (ContentBeard.claimCorners(p_175848_1_, wide, alongX)) {
                    cir.setReturnValue(wide);
                    return;
                }
                ContentLog.LOGGER.debug("A road attempt {} facing {} needs a junction corner that is already taken, so it may only be an alley", wide, facing);
            }
            if (!rolled || rdpl$joinsRoads(p_175848_1_, found, facing) || rdpl$roadWithinReach(p_175848_1_, found, facing)) {
                ContentLog.LOGGER.debug("A road attempt {} facing {} could not widen and {} an alley here, so it is refused", found, facing, rolled ? "may not be" : "did not roll");
                cir.setReturnValue(null);
                return;
            }
            ContentLog.LOGGER.debug("A road attempt {} facing {} rolls an alley rather than being refused", found, facing);
        }
        if (3 < BeardRoads.pathMinimumWidth()) {
            cir.setReturnValue(null);
            return;
        }
        if (!ContentBeard.claimCorners(p_175848_1_, found, alongX)) {
            ContentLog.LOGGER.debug("A road attempt {} facing {} needs a junction corner that is already taken, so it is refused", found, facing);
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "buildComponent", at = @At("HEAD")) private void rdpl$alleyStops(StructureComponent componentIn, List<StructureComponent> listIn, Random rand, CallbackInfo ci) {
        CityGrowth.alleyLaying(BeardRoads.roadNarrow(getBoundingBox(), BeardPlots.roadAlongX(this)));
    }

    @Inject(method = "buildComponent", at = @At("RETURN")) private void rdpl$alleyStopsEnd(StructureComponent componentIn, List<StructureComponent> listIn, Random rand, CallbackInfo ci) { CityGrowth.alleyLaying(false); }

    @SuppressWarnings("ConstantConditions") @Inject(method = "buildComponent", at = @At("HEAD")) private void rdpl$branchAtBlocks(StructureComponent componentIn, List<StructureComponent> listIn, Random rand, CallbackInfo ci) {
        if (!ContentBeard.wanted() || !(componentIn instanceof StructureVillagePieces.Start) || !(this instanceof IVillageBlock) || CityLayout.drawn()) { return; }
        int block = ((IVillageBlock) this).rdpl$block();
        if (block <= 0) { return; }
        StructureBoundingBox box = getBoundingBox();
        boolean alongX = BeardPlots.roadAlongX(this);
        if (BeardRoads.roadNarrow(box, alongX) || CityGrowth.bulbWide(this)) { return; }
        int full = BeardRoads.pathFullWidth();
        int interval = 2 * block + full;
        int least = alongX ? box.minX : box.minZ;
        int most = alongX ? box.maxX : box.maxZ;
        int branched = 0;
        for (int at = least + interval; at + 2 + full <= most; at += interval) {
            for (int side = 0; side < 2; side++) {
                if (rand.nextInt(3) == 0) { continue; }
                boolean outward = side == 1;
                int x = alongX ? at : (outward ? box.maxX + 1 : box.minX - 1);
                int z = alongX ? (outward ? box.maxZ + 1 : box.minZ - 1) : at;
                EnumFacing facing = alongX ? (outward ? EnumFacing.SOUTH : EnumFacing.NORTH) : (outward ? EnumFacing.EAST : EnumFacing.WEST);
                if (IVillagePieces.rdpl$roadPiece((StructureVillagePieces.Start) componentIn, listIn, rand, x, box.minY, z, facing, getComponentType()) != null) { branched++; }
            }
        }
        if (branched > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} branches {} street(s) along its length at every {} blocks, the depth of two of its {} deep blocks and a road", box.minX, box.minZ, branched, interval, block); }
    }

    @Unique private static boolean rdpl$lineUp(List<StructureComponent> own, StructureBoundingBox found, boolean alongX, int half) {
        List<StructureComponent> pieces = ContentBeard.everyone(own);
        int center = alongX ? (found.minZ + found.maxZ) / 2 : (found.minX + found.maxX) / 2;
        StructureBoundingBox held = null;
        int nearest = Integer.MAX_VALUE;
        for (StructureComponent other : pieces) {
            if (!(other instanceof StructureVillagePieces.Path) || CityGrowth.bulbWide(other)) { continue; }
            StructureBoundingBox met = other.getBoundingBox();
            if (BeardPlots.roadAlongX(met) != alongX) { continue; }
            if (BeardRoads.roadNarrow(met, alongX)) { continue; }
            boolean acrossed = alongX ? met.maxZ >= found.minZ - half && met.minZ <= found.maxZ + half : met.maxX >= found.minX - half && met.minX <= found.maxX + half;
            if (!acrossed) { continue; }
            int gap = alongX ? Math.max(met.minX - found.maxX, found.minX - met.maxX) : Math.max(met.minZ - found.maxZ, found.minZ - met.maxZ);
            if (gap < 2 || gap > CityGrowth.march() || gap >= nearest) { continue; }
            nearest = gap;
            held = met;
        }
        if (held == null) { return true; }
        int delta = (alongX ? (held.minZ + held.maxZ) / 2 : (held.minX + held.maxX) / 2) - center;
        if (delta == 0) { return true; }
        StructureBoundingBox slid = new StructureBoundingBox(found);
        if (alongX) {
            slid.minZ += delta;
            slid.maxZ += delta;
        }
        else {
            slid.minX += delta;
            slid.maxX += delta;
        }
        for (StructureComponent taken : pieces) {
            if (taken instanceof StructureVillagePieces.Path && BeardRoads.roadNarrow(taken.getBoundingBox(), BeardPlots.roadAlongX(taken))) { continue; }
            if (taken.getBoundingBox().intersectsWith(slid.minX, slid.minZ, slid.maxX, slid.maxZ)) {
                ContentLog.LOGGER.debug("A road attempt {} cannot slide {} to line up with the road at {}, {} along its corridor, so it is refused", found, delta, held.minX, held.minZ);
                return false;
            }
        }
        ContentLog.LOGGER.debug("A road attempt {} slides {} to line up with the road at {}, {} along its corridor", found, delta, held.minX, held.minZ);
        if (alongX) {
            found.minZ = slid.minZ;
            found.maxZ = slid.maxZ;
        }
        else {
            found.minX = slid.minX;
            found.maxX = slid.maxX;
        }
        return true;
    }

    @Unique private static boolean rdpl$tooNear(StructureVillagePieces.Start start, List<StructureComponent> own, StructureBoundingBox wide, EnumFacing facing) {
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        StructureBoundingBox ahead = new StructureBoundingBox(wide);
        int reach = rdpl$fromWell(own, wide, facing) ? ContentBeard.attachGap() * 2 : 0;
        if (alongX) { if (facing.getXOffset() > 0) { ahead.maxX += reach; } else { ahead.minX -= reach; } }
        else { if (facing.getZOffset() > 0) { ahead.maxZ += reach; } else { ahead.minZ -= reach; } }
        StructureBoundingBox held = ContentBeard.beside(own, ahead, alongX, start);
        if (held == null) { return false; }
        ContentLog.LOGGER.debug("A road attempt {} facing {} would run beside the road at {}, {}, under the {} block spacing the plots on both sides need, so it may only be an alley", wide, facing, held.minX, held.minZ, ContentVillages.blockOf(start) + ContentVillages.blockOf(ContentBeard.roadAt(own, held)));
        return true;
    }

    @Unique private static boolean rdpl$fromWell(List<StructureComponent> own, StructureBoundingBox box, EnumFacing facing) {
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        int back = alongX ? (facing.getXOffset() > 0 ? box.minX - 1 : box.maxX + 1) : (facing.getZOffset() > 0 ? box.minZ - 1 : box.maxZ + 1);
        for (StructureComponent other : own) {
            if (!(other instanceof StructureVillagePieces.Well)) { continue; }
            StructureBoundingBox well = other.getBoundingBox();
            if (alongX ? (back >= well.minX && back <= well.maxX && box.maxZ >= well.minZ && box.minZ <= well.maxZ) : (back >= well.minZ && back <= well.maxZ && box.maxX >= well.minX && box.minX <= well.maxX)) { return true; }
        }
        return false;
    }

    @Unique private static boolean rdpl$widensPast(List<StructureComponent> pieces, StructureBoundingBox wide, EnumFacing facing) {
        List<StructureBoundingBox> plazas = BeardPlots.plazaSquares(pieces);
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        for (StructureComponent other : pieces) {
            StructureBoundingBox held = other.getBoundingBox();
            if (!held.intersectsWith(wide.minX, wide.minZ, wide.maxX, wide.maxZ)) { continue; }
            if (other instanceof StructureVillagePieces.Path && BeardRoads.roadNarrow(held, BeardPlots.roadAlongX(held))) { continue; }
            if (plazas.isEmpty() || !(other instanceof StructureVillagePieces.Path)) { return false; }
            if (BeardPlots.roadAlongX(held) == alongX) { return false; }
            boolean covered = false;
            for (StructureBoundingBox plaza : plazas) {
                if (Math.max(wide.minX, held.minX) < plaza.minX || Math.min(wide.maxX, held.maxX) > plaza.maxX) { continue; }
                if (Math.max(wide.minZ, held.minZ) < plaza.minZ || Math.min(wide.maxZ, held.maxZ) > plaza.maxZ) { continue; }
                covered = true;
                break;
            }
            if (!covered) { return false; }
        }
        return true;
    }

    @Unique private static boolean rdpl$roadWithinReach(List<StructureComponent> pieces, StructureBoundingBox alley, EnumFacing facing) {
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        boolean onward = facing == EnumFacing.EAST || facing == EnumFacing.SOUTH;
        int end = alongX ? (onward ? alley.maxX : alley.minX) : (onward ? alley.maxZ : alley.minZ);
        int reach = ContentBeard.attachGap();
        for (StructureComponent other : pieces) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox met = other.getBoundingBox();
            boolean lined = alongX ? met.maxZ >= alley.minZ && met.minZ <= alley.maxZ : met.maxX >= alley.minX && met.minX <= alley.maxX;
            if (!lined) { continue; }
            int away = onward ? (alongX ? met.minX : met.minZ) - end : end - (alongX ? met.maxX : met.maxZ);
            if (away > 0 && away <= reach) { return true; }
        }
        return false;
    }

    @Unique private static boolean rdpl$joinsRoads(List<StructureComponent> pieces, StructureBoundingBox alley, EnumFacing facing) {
        int minX = alley.minX;
        int maxX = alley.maxX;
        int minZ = alley.minZ;
        int maxZ = alley.maxZ;
        switch (facing) {
            case NORTH: minZ = maxZ = alley.minZ - 1; break;
            case SOUTH: minZ = maxZ = alley.maxZ + 1; break;
            case WEST: minX = maxX = alley.minX - 1; break;
            default: minX = maxX = alley.maxX + 1;
        }
        for (StructureComponent other : pieces) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }
            if (other.getBoundingBox().intersectsWith(minX, minZ, maxX, maxZ)) { return true; }
        }
        return false;
    }

    @Unique private static boolean rdpl$acrossPlaza(List<StructureComponent> components, StructureBoundingBox road) {
        if (!BeardRoads.pathChosen()) { return false; }
        int reach = BeardRoads.pathFullWidth();
        for (StructureBoundingBox well : BeardPlots.wellBoxes(components)) {
            if (road.maxX < well.minX - reach || road.minX > well.maxX + reach || road.maxZ < well.minZ - reach || road.minZ > well.maxZ + reach) { continue; }
            boolean alongX = road.maxX - road.minX >= road.maxZ - road.minZ;
            boolean radial = alongX ? road.maxZ >= well.minZ && road.minZ <= well.maxZ : road.maxX >= well.minX && road.minX <= well.maxX;
            if (!radial) { return true; }
        }
        return false;
    }

    @Redirect(method = "addComponentParts", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;getTopSolidOrLiquidBlock(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/BlockPos;"))
    private BlockPos rdpl$layInWindow(World world, BlockPos pos) { return GroundLevel.inWindow(world, pos); }

    @Inject(method = "addComponentParts", at = @At("HEAD"), cancellable = true) private void rdpl$grade(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn, CallbackInfoReturnable<Boolean> cir) {
        if (!ContentBeard.wanted()) { return; }
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The game asked the road at {}, {} to build itself for the patch of land from {}, {} to {}, {}", this.getBoundingBox().minX, this.getBoundingBox().minZ, structureBoundingBoxIn.minX, structureBoundingBoxIn.minZ, structureBoundingBoxIn.maxX, structureBoundingBoxIn.maxZ); }
        rdpl$pave(worldIn, structureBoundingBoxIn);
        cir.setReturnValue(true);
    }

    @Unique private void rdpl$pave(World world, StructureBoundingBox clip) {
        IBlockState deck = getBiomeSpecificBlockState(Objects.requireNonNull(Blocks.PLANKS).getDefaultState());
        if (deck.getMaterial() != Material.WOOD) { deck = Objects.requireNonNull(Blocks.PLANKS).getDefaultState(); }
        BeardRoads.pave(this, world, clip,
                BeardRoads.pathBlock("villagePathBlock", Config.worldgen.villagePathBlock, getBiomeSpecificBlockState(Objects.requireNonNull(Blocks.GRASS_PATH).getDefaultState())),
                BeardRoads.pathBlock("villagePathSupportBlock", Config.worldgen.villagePathSupportBlock, getBiomeSpecificBlockState(Objects.requireNonNull(Blocks.GRAVEL).getDefaultState())),
                BeardRoads.pathBlock("villagePathBridgeBlock", Config.worldgen.villagePathBridgeBlock, deck),
                BeardRoads.pathChosen());
    }

    @Override public void rdpl$layout(BeardRoads.Grade grade) { this.rdpl$stored = grade; }

    @Override @Nullable public BeardRoads.Grade rdpl$layout() { return this.rdpl$stored; }

    @Override public void rdpl$repave(World world, StructureBoundingBox clip) { rdpl$pave(world, clip); }

    @Inject(method = "writeStructureToNBT", at = @At("TAIL")) private void rdpl$keepLayout(NBTTagCompound tagCompound, CallbackInfo ci) { if (rdpl$stored != null) { rdpl$stored.write(tagCompound); } }

    @Inject(method = "readStructureFromNBT", at = @At("TAIL")) private void rdpl$loadLayout(NBTTagCompound tagCompound, TemplateManager p_143011_2_, CallbackInfo ci) { rdpl$stored = BeardRoads.Grade.read(tagCompound); }
}
