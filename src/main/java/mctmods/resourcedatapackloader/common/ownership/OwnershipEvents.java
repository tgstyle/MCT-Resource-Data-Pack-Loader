package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimMessages;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimProtection;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimTools;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimWorldProtection;
import mctmods.resourcedatapackloader.common.ownership.claims.ContentClaims;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;

public final class OwnershipEvents {
    private OwnershipEvents() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, ContentOwnership::onPlace);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, ContentOwnership::onBreak);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, ContentOwnership::onDetonate);
        NeoForge.EVENT_BUS.addListener(ContentOwnership::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(ContentOwnership::onTick);
        NeoForge.EVENT_BUS.addListener(ContentOwnership::onLogin);
        NeoForge.EVENT_BUS.addListener(ContentOwnership::onCommand);
        NeoForge.EVENT_BUS.addListener(ContentOwnership::onLevelLoad);
        NeoForge.EVENT_BUS.register(ClaimProtection.class);
        NeoForge.EVENT_BUS.register(ClaimMessages.class);
        NeoForge.EVENT_BUS.register(ClaimWorldProtection.class);
        NeoForge.EVENT_BUS.register(ClaimTools.class);
        NeoForge.EVENT_BUS.register(ContentClaims.class);
    }
}
