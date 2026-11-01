package mctmods.resourcedatapackloader.common.rubic.regionlib.api.region.interfaces;

import java.util.function.Function;

public interface IHeaderDataEntryProvider<H extends IHeaderDataEntry, K extends IKey> extends Function<K, H> { int getEntryByteCount(); }
