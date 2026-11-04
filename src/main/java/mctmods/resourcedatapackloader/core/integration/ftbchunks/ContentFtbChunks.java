package mctmods.resourcedatapackloader.core.integration.ftbchunks;

import mctmods.resourcedatapackloader.common.ownership.claims.ForeignClaimsHolder;
import mctmods.resourcedatapackloader.common.pack.definitions.PackRequirements;
import mctmods.resourcedatapackloader.core.util.ContentLog;

public final class ContentFtbChunks {
    public static final String MOD_ID = "ftbchunks";

    private ContentFtbChunks() {}

    public static void install() {
        if (!PackRequirements.modLoaded(MOD_ID)) { return; }
        ContentLog.LOGGER.info("FTB Chunks is loaded, so a pack can have claims yield to its chunk claims with claims ftbPolicy");
        ForeignClaimsHolder.set(new FtbChunksClaims());
    }
}
