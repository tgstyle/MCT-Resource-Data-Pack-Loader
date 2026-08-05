package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentLocate;

import net.minecraft.command.CommandLocate;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(CommandLocate.class)
public abstract class MixinCommandLocate {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private void rdpl$packStructures(MinecraftServer server, ICommandSender sender, String[] args, CallbackInfo ci) {
        if (args.length != 1) { return; }

        World world = sender.getEntityWorld();
        if (!ContentLocate.names(world).contains(args[0])) { return; }

        BlockPos found = ContentLocate.nearest(world, args[0], sender.getPosition());
        if (found == null) { return; }

        sender.sendMessage(new TextComponentTranslation("commands.locate.success", args[0], found.getX(), found.getZ()));
        ci.cancel();
    }

    @Inject(method = "getTabCompletions", at = @At("RETURN"), cancellable = true)
    private void rdpl$packNames(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos, CallbackInfoReturnable<List<String>> cir) {
        if (args.length != 1) { return; }

        List<String> held = cir.getReturnValue();
        for (String name : ContentLocate.names(sender.getEntityWorld())) {
            if (name.toLowerCase().startsWith(args[0].toLowerCase()) && !held.contains(name)) { held.add(name); }
        }
    }
}
