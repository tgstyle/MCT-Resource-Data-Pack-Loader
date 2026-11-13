package mctmods.resourcedatapackloader.common.environment;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;
import mctmods.resourcedatapackloader.common.util.entity.DamageSources;

import java.util.Collection;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class ContentDarkDamage {
    private static final String GRUE = "grue";
    private static final ResourceLocation DARKNESS = ResourceLocation.parse("resourcedatapackloader:darkness");
    private static boolean wanted;

    private ContentDarkDamage() {}

    public static void arm(Collection<DimensionDef> defs) { wanted = !ModList.get().isLoaded(GRUE) && defs.stream().anyMatch(def -> def.traits().darkDamage() != null); }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (wanted && event.getEntity() instanceof ServerPlayer player) { tick(player); }
    }

    @SuppressWarnings("deprecation")
    private static void tick(ServerPlayer player) {
        DimensionTraitsDef.DarkDamage dark = ContentWeather.traits(player.level()).darkDamage();
        if (dark == null || player.isCreative() || player.isSpectator() || player.tickCount % dark.interval() != 0) { return; }
        if (player.getLightLevelDependentMagicValue() > dark.brightness()) { return; }
        Holder<DamageType> type = DamageSources.type(player.level(), DARKNESS);
        player.hurt(type == null ? player.damageSources().magic() : new DamageSource(type), dark.damage());
    }
}
