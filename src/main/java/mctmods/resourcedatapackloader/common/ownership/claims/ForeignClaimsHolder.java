package mctmods.resourcedatapackloader.common.ownership.claims;

public final class ForeignClaimsHolder {
    private static volatile ForeignClaims current = ForeignClaims.NONE;

    private ForeignClaimsHolder() {}

    public static ForeignClaims get() { return current; }

    public static void set(ForeignClaims provider) { current = provider == null ? ForeignClaims.NONE : provider; }
}
