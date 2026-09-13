package mctmods.resourcedatapackloader.mixin.rdpl.server;

import net.minecraft.server.dedicated.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import java.util.Properties;

@Mixin(Settings.class) public interface ISettings { @Invoker("cloneProperties") Properties rdpl$cloneProperties(); }
