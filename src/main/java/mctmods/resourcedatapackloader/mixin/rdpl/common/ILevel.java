package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

@Mixin(Level.class) public interface ILevel { @Accessor("blockEntityTickers") List<TickingBlockEntity> rdpl$blockEntityTickers(); }
