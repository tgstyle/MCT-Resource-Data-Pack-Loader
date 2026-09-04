package mctmods.resourcedatapackloader.content.rubic.regionlib.lib.provider;

import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IRegion;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IRegionProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKeyProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.key.RegionKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.interfaces.ICheckedConsumer;
import mctmods.resourcedatapackloader.content.rubic.regionlib.interfaces.ICheckedFunction;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class SimpleRegionProvider<K extends IKey> implements IRegionProvider<K> {
	private final IKeyProvider keyProvider;
	private final Path directory;
	private final IRegionFactory<K> regionBuilder;
	private final SimpleRegionProvider.IRegionExistsPredicate<K> regionExists;

	public SimpleRegionProvider(IKeyProvider keyProvider, Path directory,
	                            IRegionFactory<K> regionBuilder, IRegionExistsPredicate<K> regionExists) {
		this.keyProvider = keyProvider;
		this.directory = directory;
		this.regionBuilder = regionBuilder;
		this.regionExists = regionExists;
	}

	@Override public <R> Optional<R> fromExistingRegion(K key, ICheckedFunction<? super IRegion<K>, R, IOException> func) throws IOException {
		try (IRegion<K> r = getExistingRegion(key).orElse(null)) {
			return r == null ? Optional.empty() : Optional.of(func.apply(r));
		}
	}

	@Override public <R> R fromRegion(K key, ICheckedFunction<? super IRegion<K>, R, IOException> func) throws IOException {
		try (IRegion<K> r = getRegion(key)) { return func.apply(r); }
	}

	@Override public void forRegion(K key, ICheckedConsumer<? super IRegion<K>, IOException> consumer) throws IOException {
		try (IRegion<K> r = getRegion(key)) { consumer.accept(r); }
	}

	@Override public IRegion<K> getRegion(K key) throws IOException { return regionBuilder.create(keyProvider, key.getRegionKey()); }

	@Override public Optional<IRegion<K>> getExistingRegion(K key) throws IOException {
		Path regionPath = directory.resolve(key.getRegionKey().getName());
		if (!regionExists.test(regionPath, key)) { return Optional.empty(); }
		IRegion<K> reg = regionBuilder.create(keyProvider, key.getRegionKey());
		return Optional.of(reg);
	}


	@Override public void flush() throws IOException {
	}

	@Override public void close() {
	}

	@FunctionalInterface public interface IRegionFactory<K extends IKey> { IRegion<K> create(IKeyProvider keyProvider, RegionKey key) throws IOException; }

	@FunctionalInterface public interface IRegionExistsPredicate<K extends IKey> { boolean test(Path directory, K key) throws IOException; }
}
