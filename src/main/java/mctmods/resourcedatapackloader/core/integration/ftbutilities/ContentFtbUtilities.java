package mctmods.resourcedatapackloader.core.integration.ftbutilities;

import mctmods.resourcedatapackloader.common.ownership.claims.ForeignClaimsHolder;
import mctmods.resourcedatapackloader.core.integration.CompatHooks;

public final class ContentFtbUtilities {
    private ContentFtbUtilities() {}

    public static void install() {
        if (CompatHooks.present("ftbutilities", "FTBUtilities is loaded, so a pack can have claims yield to its chunk claims with claims ftbPolicy")) { ForeignClaimsHolder.set(new FtbUtilitiesClaims()); }
    }
}
