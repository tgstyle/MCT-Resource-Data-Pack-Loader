package mctmods.resourcedatapackloader.common.leafdecay;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mctmods.resourcedatapackloader.core.util.SideUtils;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class LeafDecays {
    private static final Map<World, DecayWatch> WATCHES = new WeakHashMap<>();

    private LeafDecays() {}

    @SubscribeEvent public static void onWorldLoad(WorldEvent.Load event) {
        World world = event.getWorld();
        if (world.isRemote || WATCHES.containsKey(world)) { return; }
        DecayWatch watch = new DecayWatch();
        WATCHES.put(world, watch);
        world.addEventListener(watch);
    }

    @SubscribeEvent public static void onWorldUnload(WorldEvent.Unload event) {
        World world = event.getWorld();
        DecayWatch watch = WATCHES.remove(world);
        if (watch != null) { world.removeEventListener(watch); }
    }

    @SubscribeEvent public static void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;
        if (!SideUtils.serverEnd(event)) { return; }
        DecayWatch watch = WATCHES.get(world);
        if (watch != null && ContentLeafDecay.load()) { watch.work(world); }
    }
}
