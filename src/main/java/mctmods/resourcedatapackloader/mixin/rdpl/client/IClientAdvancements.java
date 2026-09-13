package mctmods.resourcedatapackloader.mixin.rdpl.client;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.multiplayer.ClientAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;

@Mixin(ClientAdvancements.class) public interface IClientAdvancements { @Accessor("progress") Map<AdvancementHolder, AdvancementProgress> rdpl$getProgress(); }
