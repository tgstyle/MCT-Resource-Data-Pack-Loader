package mctmods.resourcedatapackloader.content.rubic.regionlib.interfaces;


public interface ICheckedBiConsumer<T, U, E extends Throwable> { void accept(T t, U u) throws E; }
