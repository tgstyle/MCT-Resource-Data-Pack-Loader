package mctmods.resourcedatapackloader.common.locate;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.WorldWorkerManager;

abstract class LocateWorker implements WorldWorkerManager.IWorker {
    protected final EntityPlayerMP player;
    protected final World world;
    protected final String name;
    protected final Consumer<BlockPos> delivery;
    protected BlockPos best;
    protected long bestAway = Long.MAX_VALUE;
    private boolean over;

    protected LocateWorker(EntityPlayerMP player, String name, @Nullable Consumer<BlockPos> delivery) {
        this.delivery = delivery;
        this.player = player;
        this.world = player.world;
        this.name = name;
    }

    protected abstract boolean scan();

    protected abstract void release();

    @Override public final boolean hasWork() { return !over; }

    @Override public final boolean doWork() {
        if (player.hasDisconnected() || player.world != world) {
            finish();
            return false;
        }
        return scan();
    }

    protected final void finish() {
        over = true;
        release();
    }

    protected final void conclude(boolean pointing) {
        finish();
        if (delivery != null) { delivery.accept(best); }
        else if (pointing) { ContentStructureSearch.point(player, name, best); }
        else { ContentStructureSearch.arrive(player, name, best); }
    }
}
