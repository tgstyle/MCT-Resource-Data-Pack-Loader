package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimMessages;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimProtection;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimTools;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimWorldProtection;
import mctmods.resourcedatapackloader.common.ownership.claims.ContentClaims;
import mctmods.resourcedatapackloader.core.util.Compat;

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
        NeoForge.EVENT_BUS.addListener(ClaimProtection::onBreak);
        NeoForge.EVENT_BUS.addListener(ClaimProtection::onDig);
        NeoForge.EVENT_BUS.addListener(ClaimProtection::onAttack);
        NeoForge.EVENT_BUS.addListener(ClaimProtection::onPlace);
        NeoForge.EVENT_BUS.addListener(ClaimProtection::onBucketBlock);
        NeoForge.EVENT_BUS.addListener(ClaimProtection::onBucketItem);
        NeoForge.EVENT_BUS.addListener(ClaimProtection::onUseBlock);
        NeoForge.EVENT_BUS.addListener(ClaimProtection::onUseEntity);
        Compat.refuseSpecificInteractWith(ClaimProtection::refusesUse);
        NeoForge.EVENT_BUS.addListener(ClaimProtection::onHurt);
        NeoForge.EVENT_BUS.addListener(ClaimMessages::onTick);
        NeoForge.EVENT_BUS.addListener(ClaimMessages::onLogout);
        NeoForge.EVENT_BUS.addListener(ClaimWorldProtection::onDetonate);
        NeoForge.EVENT_BUS.addListener(ClaimWorldProtection::onMobGriefing);
        NeoForge.EVENT_BUS.addListener(ClaimWorldProtection::onFluidPlace);
        NeoForge.EVENT_BUS.addListener(ClaimWorldProtection::onPiston);
        NeoForge.EVENT_BUS.addListener(ClaimTools::onClickBlock);
        NeoForge.EVENT_BUS.addListener(ClaimTools::onClickAir);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ClaimTools::onPlace);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ClaimTools::onBreak);
        NeoForge.EVENT_BUS.addListener(ContentClaims::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(ContentClaims::onTick);
    }
}
