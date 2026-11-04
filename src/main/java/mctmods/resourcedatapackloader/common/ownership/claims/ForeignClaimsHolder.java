package mctmods.resourcedatapackloader.common.ownership.claims;

import javax.annotation.Nullable;

public final class ForeignClaimsHolder {
    private static volatile ForeignClaims current = ForeignClaims.NONE;

    private ForeignClaimsHolder() {}

    public static ForeignClaims get() { return current; }

    public static void set(@Nullable ForeignClaims provider) { current = provider == null ? ForeignClaims.NONE : provider; }
}
