package mctmods.resourcedatapackloader.common.environment;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public final class ContentDarkDamage {
    private static final DamageSource SOURCE = new DamageSource("rdpl.darkness").setDamageBypassesArmor();
    private static final String GRUE = "grue";

    private ContentDarkDamage() {}

    public static boolean enabled() {
        if (Loader.isModLoaded(GRUE)) { return false; }
        for (DimensionDef def : ContentDimensions.available().values()) { if (def.traits.darkDamage != null) { return true; } }
        return false;
    }

    @SubscribeEvent public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side != Side.SERVER) { return; }
        EntityPlayer player = event.player;
        DimensionDef def = ContentDimensions.of(player.world);
        DimensionTraitsDef.DarkDamage dark = def == null ? null : def.traits.darkDamage;
        if (dark == null || player.isCreative() || player.isSpectator() || player.ticksExisted % dark.interval != 0) { return; }
        if (player.getBrightness() <= dark.brightness) { player.attackEntityFrom(SOURCE, dark.damage); }
    }
}
