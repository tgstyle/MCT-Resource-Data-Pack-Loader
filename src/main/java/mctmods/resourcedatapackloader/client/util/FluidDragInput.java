package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.blocks.fluid.ContentFluidBlock;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = ResourceDataPackLoader.MOD_ID) public final class FluidDragInput {
    private static final float FLUID_INPUT = 0.2F;

    private FluidDragInput() {}

    @SubscribeEvent public static void onInput(InputUpdateEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.isInWater()) { return; }
        Block block = player.world.getBlockState(player.getPosition()).getBlock();
        if (!(block instanceof ContentFluidBlock) || ((ContentFluidBlock) block).behavior().creatures.drag == null) { return; }
        event.getMovementInput().moveForward *= FLUID_INPUT;
        event.getMovementInput().moveStrafe *= FLUID_INPUT;
    }
}
