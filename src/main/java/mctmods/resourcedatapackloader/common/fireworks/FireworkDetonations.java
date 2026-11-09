package mctmods.resourcedatapackloader.common.fireworks;

import mctmods.resourcedatapackloader.common.pack.definitions.items.FireworkDef;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.registries.ForgeRegistries;

public final class FireworkDetonations {
    private static final double REACH = 0.05D;

    private FireworkDetonations() {}

    public static void onGameEvent(VanillaGameEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || event.getVanillaEvent() != GameEvent.EXPLODE) {
            return;
        }
        Vec3 at = event.getEventPosition();
        for (FireworkRocketEntity rocket : level.getEntitiesOfClass(FireworkRocketEntity.class, new AABB(at, at).inflate(REACH))) {
            CompoundTag tag = rocket.getItem().getTag();
            if (tag == null || !tag.contains(FireworkRockets.TAG_SOURCE)) {
                continue;
            }
            ResourceLocation id = ResourceLocation.tryParse(tag.getString(FireworkRockets.TAG_SOURCE));
            Item item = id == null ? null : ForgeRegistries.ITEMS.getValue(id);
            FireworkDef def = item == null ? null : ContentFireworks.def(item);
            if (def != null) {
                FireworkBursts.fire(level, at.x, at.y, at.z, def.bursts());
            }
        }
    }
}
