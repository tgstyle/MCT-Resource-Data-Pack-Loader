package mctmods.resourcedatapackloader.util.ticket;

import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;

import com.google.common.collect.Lists;
import mctmods.resourcedatapackloader.util.interfaces.ITicket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TicketList {
    private final Cube cube;
    private int tickRefs = 0;
    @Nonnull private final List<ITicket> tickets = Lists.newArrayListWithCapacity(1);

    public TicketList(@Nullable Cube cube) { this.cube = cube; }

    public void remove(ITicket ticket) {
        if (cube == null) { return; }
        if (tickets.remove(ticket) && ticket.shouldTick()) {
            tickRefs--;
            assert tickRefs >= 0;
            if (tickRefs == 0) { ((IRubicWorldInternal.Server) cube.getWorld()).rdpl$removeForcedCube(cube); }
        }
    }

    public void add(ITicket ticket) {
        if (cube == null) { return; }
        if (tickets.contains(ticket)) { return; }
        tickets.add(ticket);
        tickRefs += ticket.shouldTick() ? 1 : 0;
        if (ticket.shouldTick()) {
            assert tickRefs > 0;
            if (tickRefs == 1) { ((IRubicWorldInternal.Server) cube.getWorld()).rdpl$addForcedCube(cube); }
        }
    }

    public boolean contains(ITicket ticket) { return tickets.contains(ticket); }

    public boolean shouldTick() { return tickRefs > 0; }

    public boolean canUnload() { return tickets.isEmpty(); }
}
