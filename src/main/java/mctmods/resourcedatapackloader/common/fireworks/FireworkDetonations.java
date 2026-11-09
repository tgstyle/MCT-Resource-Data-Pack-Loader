package mctmods.resourcedatapackloader.common.fireworks;

import mctmods.resourcedatapackloader.common.pack.definitions.items.FireworkDef;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.VanillaGameEvent;

public final class FireworkDetonations {
    private static final double REACH = 0.05D;

    private FireworkDetonations() {}

    public static void onGameEvent(VanillaGameEvent event) {
        Level level = event.getLevel();
        if (!(level instanceof ServerLevel server) || !event.getVanillaEvent().is(GameEvent.EXPLODE.key())) { return; }
        Vec3 at = event.getEventPosition();
        for (FireworkRocketEntity rocket : level.getEntitiesOfClass(FireworkRocketEntity.class, new AABB(at, at).inflate(REACH))) {
            CustomData data = rocket.getItem().get(DataComponents.CUSTOM_DATA);
            if (data == null) { continue; }
            Identifier id = Identifier.tryParse(data.copyTag().getStringOr(FireworkRockets.TAG_SOURCE, ""));
            if (id == null) { continue; }
            Item source = BuiltInRegistries.ITEM.getOptional(id).orElse(null);
            FireworkDef def = source == null ? null : ContentFireworks.def(source);
            if (def == null) { continue; }
            FireworkBursts.fire(server, at.x, at.y, at.z, def.bursts());
        }
    }
}
