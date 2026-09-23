package mctmods.resourcedatapackloader.mixin.vanillatweaks;

import mctmods.resourcedatapackloader.content.block.ContentChests;
import mctmods.resourcedatapackloader.content.interfaces.IChestPartner;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mixin(TileEntityChest.class) public abstract class MixinTileEntityChest extends TileEntityLockableLoot implements IChestPartner {
    @Unique private int rdpl$partner = ContentChests.UNKEYED;

    @Override public int rdpl$partner() { return rdpl$partner; }

    @Override public void rdpl$setPartner(int partner) {
        rdpl$partner = partner;
        markDirty();
    }

    @Inject(method = "readFromNBT", at = @At("RETURN")) private void rdpl$readPartner(NBTTagCompound compound, CallbackInfo ci) {
        int was = rdpl$partner;
        rdpl$partner = compound.hasKey(ContentChests.PARTNER) ? compound.getInteger(ContentChests.PARTNER) : ContentChests.UNKEYED;
        if (was != rdpl$partner && world != null && world.isRemote) { ContentChests.recheck(world, pos); }
    }

    @Inject(method = "writeToNBT", at = @At("RETURN")) private void rdpl$writePartner(NBTTagCompound compound, CallbackInfoReturnable<NBTTagCompound> cir) {
        if (rdpl$partner != ContentChests.UNKEYED) { compound.setInteger(ContentChests.PARTNER, rdpl$partner); }
    }

    @Inject(method = "isChestAt", at = @At("RETURN"), cancellable = true) private void rdpl$apart(BlockPos posIn, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && ContentChests.apart(world, pos, posIn)) { cir.setReturnValue(false); }
    }

    @Override @Nonnull public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        if (rdpl$partner != ContentChests.UNKEYED) { tag.setInteger(ContentChests.PARTNER, rdpl$partner); }
        return tag;
    }

    @Override @Nullable public SPacketUpdateTileEntity getUpdatePacket() { return rdpl$partner != ContentChests.UNKEYED ? new SPacketUpdateTileEntity(pos, 0, getUpdateTag()) : null; }

    @Override public void onDataPacket(@Nonnull NetworkManager net, SPacketUpdateTileEntity pkt) { handleUpdateTag(pkt.getNbtCompound()); }
}
