package mctmods.resourcedatapackloader.common.environment;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;
import mctmods.resourcedatapackloader.common.util.entity.DamageSources;

import java.util.Collection;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModList;

public final class ContentDarkDamage {
    private static final String GRUE = "grue";
    private static final ResourceLocation DARKNESS = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "darkness");
    private static boolean armed;

    private ContentDarkDamage() {}

    public static void arm(Collection<DimensionDef> defs) { armed = !ModList.get().isLoaded(GRUE) && defs.stream().anyMatch(def -> def.traits().darkDamage() != null); }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!armed || event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) { return; }
        tick(event.player);
    }

    private static void tick(Player player) {
        if (player.isCreative() || player.isSpectator()) { return; }
        DimensionTraitsDef.DarkDamage dark = ContentWeather.traits(player.level()).darkDamage();
        if (dark == null || player.tickCount % dark.interval() != 0 || brightness(player) > dark.brightness()) { return; }
        player.hurt(source(player), dark.damage());
    }

    @SuppressWarnings("deprecation")
    private static float brightness(Player player) {
        return player.getLightLevelDependentMagicValue();
    }

    private static DamageSource source(Player player) {
        Holder<DamageType> type = DamageSources.type(player.level(), DARKNESS);
        return type == null ? player.damageSources().magic() : new DamageSource(type);
    }
}
