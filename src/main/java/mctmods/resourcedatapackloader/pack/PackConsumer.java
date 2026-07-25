package mctmods.resourcedatapackloader.pack;

@FunctionalInterface
public interface PackConsumer {
    void accept(String namespace, String path, String contents);
}
