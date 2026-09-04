package mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces;

import mctmods.resourcedatapackloader.content.rubic.regionlib.interfaces.ICheckedConsumer;
import mctmods.resourcedatapackloader.content.rubic.regionlib.interfaces.ICheckedFunction;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.Optional;

public interface IRegionProvider<K extends IKey> extends Flushable, Closeable {
    void forRegion(K key, ICheckedConsumer<? super IRegion<K>, IOException> consumer) throws IOException;

    <R> Optional<R> fromExistingRegion(K key, ICheckedFunction<? super IRegion<K>, R, IOException> func) throws IOException;

    <R> R fromRegion(K key, ICheckedFunction<? super IRegion<K>, R, IOException> func) throws IOException;

    IRegion<K> getRegion(K key) throws IOException;

    Optional<IRegion<K>> getExistingRegion(K key) throws IOException;

}
