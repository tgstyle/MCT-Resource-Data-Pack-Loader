package mctmods.resourcedatapackloader.mixin.pulsar;

import mctmods.resourcedatapackloader.content.rubic.lighting.PulsarStandDown;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo @Mixin(targets = "com.sumirelabs.pulsar.proxy.CommonProxy", remap = false) public abstract class MixinPulsarCommonProxy {
    @Dynamic("Overrides IProxy's default in CommonProxy, which declares none of its own; the name must stay exactly as IProxy spells it")
    @SuppressWarnings("unused") public boolean isRealMainWorld(World world) { return world instanceof WorldServer && !PulsarStandDown.holds(world); }
}
