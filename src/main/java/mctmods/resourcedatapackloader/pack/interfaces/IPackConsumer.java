package mctmods.resourcedatapackloader.pack.interfaces;

@FunctionalInterface public interface IPackConsumer { void accept(String namespace, String path, String contents); }
