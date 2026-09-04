package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.village.CityGrowth;
import mctmods.resourcedatapackloader.content.village.CityLayout;
import mctmods.resourcedatapackloader.content.village.ContentVillages;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardLayout;
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
        BeardLayout.trim(box, alongX, facing, kept);
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
        if (block > 0 && !BeardLayout.fromWell(p_175848_1_, p_175848_3_ - facing.getXOffset(), p_175848_5_ - facing.getZOffset())) { least = Math.max(5, Math.min(least, (2 * block + BeardRoads.pathFullWidth() + 6) / 7)); }
        int rolled = MathHelper.getInt(random, least, least + 2);
        ContentLog.LOGGER.debug("A road rolls {} segments of 7, {} blocks", rolled, rolled * 7);
        return rolled;
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
        if (BeardLayout.acrossPlaza(p_175848_1_, found) || ContentBeard.taken(p_175848_1_, found)) {
            cir.setReturnValue(null);
            return;
        }
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        int half = (BeardRoads.pathFullWidth() - 3) / 2;
        if (!BeardLayout.lineUp(p_175848_1_, found, alongX, half)) {
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
                BeardLayout.trim(wide, alongX, facing, room);
                BeardLayout.trim(found, alongX, facing, room);
                ContentLog.LOGGER.debug("A road attempt {} facing {} widened would run into another village's piece after {} row(s), so it stops short of it", wide, facing, room);
            }
            if (!BeardLayout.tooNear(start, p_175848_1_, wide, facing) && BeardLayout.widensPast(p_175848_1_, wide, facing) && !ContentBeard.taken(p_175848_1_, wide)) {
                if (ContentBeard.claimCorners(p_175848_1_, wide, alongX)) {
                    cir.setReturnValue(wide);
                    return;
                }
                ContentLog.LOGGER.debug("A road attempt {} facing {} needs a junction corner that is already taken, so it may only be an alley", wide, facing);
            }
            if (!rolled || BeardLayout.joinsRoads(p_175848_1_, found, facing) || BeardLayout.roadWithinReach(p_175848_1_, found, facing)) {
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
        BeardLayout.branchAtBlocks(this, (StructureVillagePieces.Start) componentIn, listIn, rand, block);
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
