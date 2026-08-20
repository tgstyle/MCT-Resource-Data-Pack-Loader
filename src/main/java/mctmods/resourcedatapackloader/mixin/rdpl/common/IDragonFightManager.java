package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.BossInfoServer;
import net.minecraft.world.end.DragonFightManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DragonFightManager.class) public interface IDragonFightManager { @Accessor("bossInfo") BossInfoServer rdpl$getBossInfo(); }
