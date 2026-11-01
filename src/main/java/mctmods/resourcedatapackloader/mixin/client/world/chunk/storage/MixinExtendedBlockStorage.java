package mctmods.resourcedatapackloader.mixin.client.world.chunk.storage;

import mctmods.resourcedatapackloader.core.integration.optifine.IOptifineExtendedBlockStorage;

import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ExtendedBlockStorage.class) public abstract class MixinExtendedBlockStorage implements IOptifineExtendedBlockStorage {}
