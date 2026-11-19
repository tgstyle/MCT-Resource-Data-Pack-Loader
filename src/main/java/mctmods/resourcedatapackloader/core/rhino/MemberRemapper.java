package mctmods.resourcedatapackloader.core.rhino;

public interface MemberRemapper {
    String[] readableNames(Class<?> owner, String runtimeName);
}
