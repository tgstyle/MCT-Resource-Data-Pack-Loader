package mctmods.resourcedatapackloader.mixin.block;

import mctmods.resourcedatapackloader.common.entities.ai.IPathNodeAsker;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Block.class) public abstract class MixinBlockPathNode implements IPathNodeAsker {
    @Unique private byte rdpl$asksWhere;

    @Override public byte rdpl$getAsksWhere() { return rdpl$asksWhere; }

    @Override public void rdpl$setAsksWhere(byte asks) { this.rdpl$asksWhere = asks; }
}
