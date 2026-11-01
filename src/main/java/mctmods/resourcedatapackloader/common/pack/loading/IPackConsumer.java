package mctmods.resourcedatapackloader.common.pack.loading;

@FunctionalInterface public interface IPackConsumer { void accept(String namespace, String path, String contents); }
