package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IMinMaxHeight;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(RenderGlobal.class) public class MixinRenderGlobalOptifineE {
    @Shadow private WorldClient world;

    @Dynamic @ModifyConstant(method = "setupTerrain", constant = @Constant(intValue = 256), require = 0, expect = 0) public int getMaxWorldHeight(int _256) {
        return ((IMinMaxHeight) world).rdpl$getMaxHeight();
    }
}
