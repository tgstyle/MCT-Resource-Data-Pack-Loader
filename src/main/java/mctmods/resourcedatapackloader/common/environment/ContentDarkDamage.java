package mctmods.resourcedatapackloader.common.environment;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.pack.definitions.PackRequirements;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;
import mctmods.resourcedatapackloader.common.util.entity.DamageSources;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class ContentDarkDamage {
    private static final String GRUE = "grue";
    private static final Identifier DARKNESS = Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "darkness");
    private static boolean on;

    private ContentDarkDamage() {}

    public static void load() { on = !PackRequirements.modLoaded(GRUE) && ContentDimensions.all().stream().anyMatch(def -> def.traits().darkDamage() != null); }

    @SuppressWarnings("deprecation") public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!on || !(event.getEntity() instanceof ServerPlayer player) || player.isCreative() || player.isSpectator()) { return; }
        DimensionDef def = ContentDimensions.def(player.level());
        DimensionTraitsDef.DarkDamage dark = def == null ? null : def.traits().darkDamage();
        if (dark == null || player.tickCount % dark.interval() != 0 || player.getLightLevelDependentMagicValue() > dark.brightness()) { return; }
        ServerLevel level = player.level();
        player.hurtServer(level, source(player, level), dark.damage());
    }

    private static DamageSource source(ServerPlayer player, ServerLevel level) {
        Holder<DamageType> type = DamageSources.type(level, DARKNESS);
        return type == null ? player.damageSources().magic() : new DamageSource(type);
    }
}
