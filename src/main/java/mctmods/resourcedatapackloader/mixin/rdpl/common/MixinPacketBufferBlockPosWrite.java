package mctmods.resourcedatapackloader.mixin.rdpl.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PacketBuffer.class) public abstract class MixinPacketBufferBlockPosWrite {
    @Shadow public abstract long readLong();

    @Shadow public abstract ByteBuf writeLong(long p_writeLong_1_);

    @Shadow public abstract int readVarInt();

    @Shadow public abstract PacketBuffer writeVarInt(int input);

    /**
     * @author tgstyle
     * @reason Decode the out-of-range Y sentinel written by writeBlockPos back into a full-height position.
     */
    @Overwrite public BlockPos readBlockPos() {
        long data = this.readLong();
        BlockPos pos = BlockPos.fromLong(data);
        if (pos.getY() == -2048) { return new BlockPos(pos.getX(), this.readVarInt(), pos.getZ()); }
        else { return pos; }
    }

    /**
     * @author tgstyle
     * @reason Encode positions outside the packed 12-bit Y range with a sentinel plus a variant so cube heights survive the wire.
     */
    @Overwrite public PacketBuffer writeBlockPos(BlockPos pos) {
        int y = pos.getY();
        if (y <= 2047 && y >= -2047) { this.writeLong(pos.toLong()); }
        else {
            this.writeLong(new BlockPos(pos.getX(), -2048, pos.getZ()).toLong());
            this.writeVarInt(y);
        }
        return (PacketBuffer) (Object) this;
    }
}
