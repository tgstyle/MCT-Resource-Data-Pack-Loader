package mctmods.resourcedatapackloader.core.rhino;

public final class NullMemberRemapper implements MemberRemapper {
    public static final NullMemberRemapper INSTANCE = new NullMemberRemapper();

    private static final String[] NONE = new String[0];

    private NullMemberRemapper() {}

    @Override
    public String[] readableNames(Class<?> owner, String runtimeName) {
        return NONE;
    }
}
