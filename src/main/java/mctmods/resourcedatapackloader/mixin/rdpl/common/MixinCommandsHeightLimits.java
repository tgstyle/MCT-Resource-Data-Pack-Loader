package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.command.CommandClone;
import net.minecraft.command.CommandCompare;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin({CommandClone.class, CommandCompare.class}) public class MixinCommandsHeightLimits {
    @Group(name = "command_getMinY", min = 2, max = 2) @ModifyConstant(
            method = "execute",
            constant = @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO, ordinal = 0),
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/world/gen/structure/StructureBoundingBox;minY:I", opcode = Opcodes.GETFIELD)))
    private int command_getMinY1(int orig, MinecraftServer server, ICommandSender sender, String[] args) {
        return ((IRubicWorld) sender.getEntityWorld()).rdpl$getMinHeight();
    }

    @Group(name = "command_getMinY") @ModifyConstant(
            method = "execute",
            constant = @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO, ordinal = 1),
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/world/gen/structure/StructureBoundingBox;minY:I", opcode = Opcodes.GETFIELD)))
    private int command_getMinY2(int orig, MinecraftServer server, ICommandSender sender, String[] args) {
        return ((IRubicWorld) sender.getEntityWorld()).rdpl$getMinHeight();
    }

    @Group(name = "command_getMaxY", min = 2, max = 2) @ModifyConstant(
            method = "execute",
            constant = @Constant(intValue = 256, ordinal = 0),
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/world/gen/structure/StructureBoundingBox;maxY:I", opcode = Opcodes.GETFIELD)))
    private int command_getMaxY1(int orig, MinecraftServer server, ICommandSender sender, String[] args) {
        return ((IRubicWorld) sender.getEntityWorld()).rdpl$getMaxHeight();
    }

    @Group(name = "command_getMaxY") @ModifyConstant(
            method = "execute",
            constant = @Constant(intValue = 256, ordinal = 1),
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/world/gen/structure/StructureBoundingBox;maxY:I", opcode = Opcodes.GETFIELD)))
    private int command_getMaxY2(int orig, MinecraftServer server, ICommandSender sender, String[] args) {
        return ((IRubicWorld) sender.getEntityWorld()).rdpl$getMaxHeight();
    }
}
