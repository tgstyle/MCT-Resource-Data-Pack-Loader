package mctmods.resourcedatapackloader.content.rubic.regionlib.interfaces;


public interface ICheckedFunction<T, R, E extends Throwable> { R apply(T t) throws E; }
