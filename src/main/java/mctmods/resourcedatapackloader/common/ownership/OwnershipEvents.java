package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimMessages;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimProtection;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimSettings;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimTools;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimWorldProtection;
import mctmods.resourcedatapackloader.common.ownership.claims.ContentClaims;
import mctmods.resourcedatapackloader.core.integration.ftbteams.ContentFtbTeams;

import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

public final class OwnershipEvents {
    private OwnershipEvents() {
    }

    public static void register(IEventBus modBus) {
        MinecraftForge.EVENT_BUS.addGenericListener(LevelChunk.class, OwnedBlocksProvider::onAttach);
        modBus.addListener(OwnedBlocksProvider::register);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, ContentOwnership::onPlace);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, ContentOwnership::onBreak);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, ContentOwnership::onDetonate);
        MinecraftForge.EVENT_BUS.addListener(ContentOwnership::onChunkLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentOwnership::onTick);
        MinecraftForge.EVENT_BUS.addListener(ContentOwnership::onLogin);
        MinecraftForge.EVENT_BUS.addListener(ContentOwnership::onCommand);
        MinecraftForge.EVENT_BUS.addListener(ContentOwnership::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ClaimProtection::onBreak);
        MinecraftForge.EVENT_BUS.addListener(ClaimProtection::onDig);
        MinecraftForge.EVENT_BUS.addListener(ClaimProtection::onAttack);
        MinecraftForge.EVENT_BUS.addListener(ClaimProtection::onPlace);
        MinecraftForge.EVENT_BUS.addListener(ClaimProtection::onBucketBlock);
        MinecraftForge.EVENT_BUS.addListener(ClaimProtection::onBucketItem);
        MinecraftForge.EVENT_BUS.addListener(ClaimProtection::onUseBlock);
        MinecraftForge.EVENT_BUS.addListener(ClaimProtection::onUseEntity);
        MinecraftForge.EVENT_BUS.addListener(ClaimProtection::onUseEntitySpecific);
        MinecraftForge.EVENT_BUS.addListener(ClaimProtection::onHurt);
        MinecraftForge.EVENT_BUS.addListener(ClaimMessages::onTick);
        MinecraftForge.EVENT_BUS.addListener(ClaimMessages::onLogout);
        MinecraftForge.EVENT_BUS.addListener(ClaimWorldProtection::onDetonate);
        MinecraftForge.EVENT_BUS.addListener(ClaimWorldProtection::onMobGriefing);
        MinecraftForge.EVENT_BUS.addListener(ClaimWorldProtection::onFluidPlace);
        MinecraftForge.EVENT_BUS.addListener(ClaimWorldProtection::onPiston);
        MinecraftForge.EVENT_BUS.addListener(ClaimTools::onClickBlock);
        MinecraftForge.EVENT_BUS.addListener(ClaimTools::onClickAir);
        MinecraftForge.EVENT_BUS.addListener(ClaimTools::onPlace);
        MinecraftForge.EVENT_BUS.addListener(ClaimTools::onBreak);
        MinecraftForge.EVENT_BUS.addListener(ContentClaims::onChunkLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentClaims::onTick);
        MinecraftForge.EVENT_BUS.addListener((ServerStartingEvent event) -> { ContentFtbTeams.serverStarting(); ClaimSettings.announceForeign(); });
    }
}
