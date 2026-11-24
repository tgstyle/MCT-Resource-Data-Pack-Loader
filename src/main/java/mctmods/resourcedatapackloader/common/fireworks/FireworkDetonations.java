package mctmods.resourcedatapackloader.common.fireworks;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.pack.definitions.items.FireworkDef;
import mctmods.resourcedatapackloader.common.util.Nbts;

import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mctmods.resourcedatapackloader.core.util.SideUtils;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ResourceDataPackLoader.MOD_ID) public final class FireworkDetonations {
    private static final Map<EntityFireworkRocket, String> LIVE = new WeakHashMap<>();

    private FireworkDetonations() {}

    @SubscribeEvent public static void onJoin(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote || !(event.getEntity() instanceof EntityFireworkRocket)) {
            return;
        }
        EntityFireworkRocket rocket = (EntityFireworkRocket) event.getEntity();
        NBTTagCompound saved = rocket.writeToNBT(new NBTTagCompound());
        NBTTagCompound stack = saved.getCompoundTag("FireworksItem");
        if (!stack.hasKey(Nbts.STACK_TAG, Constants.NBT.TAG_COMPOUND)) {
            return;
        }
        String source = stack.getCompoundTag(Nbts.STACK_TAG).getString(FireworkRockets.TAG_SOURCE);
        if (!source.isEmpty()) {
            LIVE.put(rocket, source);
        }
    }

    @SubscribeEvent public static void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;
        if (!SideUtils.serverEnd(event) || LIVE.isEmpty()) {
            return;
        }
        for (Map.Entry<EntityFireworkRocket, String> entry : new ArrayList<>(LIVE.entrySet())) {
            EntityFireworkRocket rocket = entry.getKey();
            if (rocket == null || rocket.world != world) {
                continue;
            }
            if (rocket.isDead) {
                if (rocket.fireworkAge >= rocket.lifetime) {
                    detonate((WorldServer) world, rocket, entry.getValue());
                }
                LIVE.remove(rocket);
            } else if (!rocket.isAddedToWorld()) {
                LIVE.remove(rocket);
            }
        }
    }

    private static void detonate(WorldServer world, EntityFireworkRocket rocket, String source) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(source));
        FireworkDef def = item == null ? null : ContentFireworks.def(item);
        if (def != null) {
            FireworkBursts.fire(world, rocket.posX, rocket.posY, rocket.posZ, def.bursts);
        }
    }
}
