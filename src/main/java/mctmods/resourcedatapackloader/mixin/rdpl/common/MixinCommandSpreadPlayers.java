package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.command.CommandSpreadPlayers$Position") public class MixinCommandSpreadPlayers {
    @ModifyConstant(method = "getSpawnY", constant = @Constant(doubleValue = 256)) private double rdpl$spawnSearchTop(double orig, World worldIn) { return GenHeights.ceiling(worldIn, (int) orig); }

    @ModifyConstant(method = "getSpawnY", constant = @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO)) private int rdpl$spawnSearchFloor(int orig, World worldIn) { return GenHeights.floor(worldIn, orig); }

    @ModifyConstant(method = "getSpawnY", constant = @Constant(intValue = 257)) private int rdpl$spawnNotFound(int orig, World worldIn) { return GenHeights.ceiling(worldIn, orig - 1) + 1; }

    @ModifyConstant(method = "isSafe", constant = @Constant(doubleValue = 256)) private double rdpl$safeSearchTop(double orig, World worldIn) { return GenHeights.ceiling(worldIn, (int) orig); }

    @ModifyConstant(method = "isSafe", constant = @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO)) private int rdpl$safeSearchFloor(int orig, World worldIn) { return GenHeights.floor(worldIn, orig); }
}
