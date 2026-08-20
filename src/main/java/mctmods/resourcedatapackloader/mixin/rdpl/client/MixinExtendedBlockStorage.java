package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.core.optifine.interfaces.IOptifineExtendedBlockStorage;

import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ExtendedBlockStorage.class) public abstract class MixinExtendedBlockStorage implements IOptifineExtendedBlockStorage {}
