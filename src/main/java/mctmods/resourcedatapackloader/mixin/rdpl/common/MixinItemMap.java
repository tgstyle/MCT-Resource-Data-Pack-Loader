package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.item.ItemMap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ItemMap.class) public class MixinItemMap {
    @Unique private static final int rdpl$SCAN_DEPTH = 64;
    @Unique private int rdpl$scanFloor;

    @Redirect(method = "updateMapData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getHeightValue(II)I"), require = 1) private int rdpl$recordScanFloor(Chunk chunk, int x, int z) {
        int h = chunk.getHeightValue(x, z);
        World world = chunk.getWorld();
        if (!GenHeights.rubic(world)) { rdpl$scanFloor = 0; }
        else {
            int min = GenHeights.floor(world, 0);
            rdpl$scanFloor = h <= min ? min : Math.max(min, h - 1 - rdpl$SCAN_DEPTH);
        }
        return h;
    }

    @ModifyConstant(method = "updateMapData", constant = @Constant(intValue = 1, ordinal = 1), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getHeightValue(II)I")), require = 1) private int rdpl$bedrockTest(int orig) { return rdpl$scanFloor + 1; }

    @ModifyConstant(method = "updateMapData", constant = @Constant(intValue = 0, expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getHeightValue(II)I"), to = @At(value = "INVOKE", target = "Lcom/google/common/collect/Multiset;add(Ljava/lang/Object;)Z", remap = false)), require = 3) private int rdpl$scanFloorConstant(int orig) { return rdpl$scanFloor; }
}
