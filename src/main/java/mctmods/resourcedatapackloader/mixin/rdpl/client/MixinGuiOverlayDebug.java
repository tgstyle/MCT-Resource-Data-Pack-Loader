package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.util.Coords;
import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(GuiOverlayDebug.class) public class MixinGuiOverlayDebug {
    @Shadow @Final private Minecraft mc;

    @Group(name = "getMinWorldHeight", min = 1, max = 1) @ModifyConstant(method = "call", constant = @Constant(intValue = 0, expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;isBlockLoaded(Lnet/minecraft/util/math/BlockPos;)Z"), to = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;isEmpty()Z"))) private int rdpl$getMinWorldHeight(int orig) { return GenHeights.floor(mc.world, orig); }

    @Group(name = "getMaxWorldHeight", min = 1, max = 1) @ModifyConstant(method = "call", constant = @Constant(intValue = 256), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;isBlockLoaded(Lnet/minecraft/util/math/BlockPos;)Z"), to = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;isEmpty()Z"))) private int rdpl$getMaxWorldHeight(int orig) { return GenHeights.ceiling(mc.world, orig); }

    @Redirect(method = "call", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getBiome(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/biome/BiomeProvider;)Lnet/minecraft/world/biome/Biome;")) private Biome rdpl$getBiome(Chunk chunk, BlockPos pos, BiomeProvider provider) {
        if (GenHeights.rubic(chunk.getWorld())) {
            ICube cube = ((IColumn) chunk).getCube(Coords.blockToCube(pos.getY()));
            return cube.getBiome(pos);
        }
        else { return chunk.getBiome(pos, provider); }
    }
}
