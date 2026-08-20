package mctmods.resourcedatapackloader.content.rubic.regionlib.interfaces;


public interface ICheckedConsumer<T, E extends Throwable> { void accept(T t) throws E; }
