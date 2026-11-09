package mctmods.resourcedatapackloader.common.fireworks;

import mctmods.resourcedatapackloader.common.pack.definitions.items.FireworkDef;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.VanillaGameEvent;

public final class FireworkDetonations {
    private static final double REACH = 0.05D;

    private FireworkDetonations() {}

    public static void onGameEvent(VanillaGameEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !event.getVanillaEvent().is(GameEvent.EXPLODE.key())) {
            return;
        }
        Vec3 at = event.getEventPosition();
        for (FireworkRocketEntity rocket : level.getEntitiesOfClass(FireworkRocketEntity.class, new AABB(at, at).inflate(REACH))) {
            CustomData data = rocket.getItem().get(DataComponents.CUSTOM_DATA);
            if (data == null || !data.contains(FireworkRockets.TAG_SOURCE)) {
                continue;
            }
            ResourceLocation id = ResourceLocation.tryParse(data.copyTag().getString(FireworkRockets.TAG_SOURCE));
            Item item = id == null ? null : BuiltInRegistries.ITEM.getOptional(id).orElse(null);
            FireworkDef def = item == null ? null : ContentFireworks.def(item);
            if (def != null) {
                FireworkBursts.fire(level, at.x, at.y, at.z, def.bursts());
            }
        }
    }
}
