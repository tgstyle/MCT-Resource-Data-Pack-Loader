package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MinecraftServer.class) public abstract class MixinMinecraftServer {
    @Unique private static int rdpl$across() {
        int asked = ContentPregen.spawnRadius();
        return asked < 0 ? -1 : 2 * asked + 1;
    }

    @ModifyConstant(method = "loadLevel", constant = @Constant(intValue = 11)) private int rdpl$spawnListener(int was) {
        int asked = ContentPregen.spawnRadius();
        return asked < 0 ? was : asked;
    }

    @ModifyConstant(method = "prepareLevels", constant = @Constant(intValue = 11)) private int rdpl$spawnTicket(int was) {
        int across = rdpl$across();
        return across < 0 ? was : across / 2 + 1;
    }

    @ModifyConstant(method = "prepareLevels", constant = @Constant(intValue = 441)) private int rdpl$spawnCount(int was) {
        int across = rdpl$across();
        return across < 0 ? was : across * across;
    }
}
