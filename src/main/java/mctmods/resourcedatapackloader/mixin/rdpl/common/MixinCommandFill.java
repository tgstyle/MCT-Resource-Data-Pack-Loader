package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandFill;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import java.lang.ref.WeakReference;
import javax.annotation.Nullable;

@Mixin(CommandFill.class) public class MixinCommandFill {
    @Unique @Nullable private WeakReference<IRubicWorld> rdpl$commandWorld;

    @Inject(method = "execute", at = @At(value = "HEAD"), require = 1) private void getWorldFromExecute(MinecraftServer server, ICommandSender sender, String[] args, CallbackInfo cbi) {
        rdpl$commandWorld = new WeakReference<>((IRubicWorld) sender.getEntityWorld());
    }

    @ModifyConstant(
            method = "execute",
            constant = {
                    @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO, ordinal = 0),
                    @Constant(intValue = 256, ordinal = 0) },
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=commands.fill.tooManyBlocks")), require = 2)
    private int execute_getMinHeight(int original) {
        if (rdpl$commandWorld == null) { return original; }
        IRubicWorld world = rdpl$commandWorld.get();
        if (world == null) { return original; }
        return original == 0 ? world.rdpl$getMinHeight() : world.rdpl$getMaxHeight();
    }

    @Unique private Integer rdpl$minY;
    @Unique private Integer rdpl$maxY;

    @Inject(method = "execute",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/command/ICommandSender;getEntityWorld()Lnet/minecraft/world/World;"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onGetEntityWorld(MinecraftServer server, ICommandSender sender, String[] args, CallbackInfo c,
            BlockPos blockpos, BlockPos blockpos1, Block block, IBlockState iblockstate, BlockPos minPos, BlockPos maxPos, int i) {
        rdpl$minY = minPos.getY();
        rdpl$maxY = maxPos.getY();
    }

    @Redirect(method = "execute",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isBlockLoaded(Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean isBlockLoadedCheckForHeightRangeRedirect(World world, BlockPos pos) {
        if (!((IRubicWorld) world).rdpl$isRubicWorld()) { return world.isBlockLoaded(pos); }
        if (rdpl$minY == null) {
            assert rdpl$maxY == null;
            return ((IRubicWorld) world).rdpl$isBlockColumnLoaded(pos);
        }
        for (int blockY = rdpl$minY; blockY <= rdpl$maxY; blockY += Cube.SIZE) {
            if (!world.isBlockLoaded(new BlockPos(pos.getX(), blockY, pos.getZ()))) { return false; }
        }
        return true;
    }
}
