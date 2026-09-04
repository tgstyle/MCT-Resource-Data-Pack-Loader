package mctmods.resourcedatapackloader.content.rubic.regionlib.lib.provider;

import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IRegion;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IRegionProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKeyProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.key.RegionKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.interfaces.ICheckedBiConsumer;
import mctmods.resourcedatapackloader.content.rubic.regionlib.interfaces.ICheckedConsumer;
import mctmods.resourcedatapackloader.content.rubic.regionlib.interfaces.ICheckedFunction;
import mctmods.resourcedatapackloader.content.rubic.regionlib.lib.Region;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

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
		IRegion<K> r = getExistingRegion(key).orElse(null);
		if (r != null) {
			R ret = func.apply(r);
			r.close();
			return Optional.of(ret);
		}
		return Optional.empty();
	}

	@Override public <R> R fromRegion(K key, ICheckedFunction<? super IRegion<K>, R, IOException> func) throws IOException {
		IRegion<K> r = getRegion(key);
		R ret = func.apply(r);
		r.close();
		return ret;
	}

	@Override public void forRegion(K key, ICheckedConsumer<? super IRegion<K>, IOException> consumer) throws IOException {
		IRegion<K> r = getRegion(key);
		consumer.accept(r);
		r.close();
	}

	@Override public IRegion<K> getRegion(K key) throws IOException { return regionBuilder.create(keyProvider, key.getRegionKey()); }

	@Override public Optional<IRegion<K>> getExistingRegion(K key) throws IOException {
		Path regionPath = directory.resolve(key.getRegionKey().getName());
		if (!regionExists.test(regionPath, key)) { return Optional.empty(); }
		IRegion<K> reg = regionBuilder.create(keyProvider, key.getRegionKey());
		return Optional.of(reg);
	}

	@Override public void forAllRegions(ICheckedBiConsumer<RegionKey, ? super IRegion<K>, IOException> consumer) throws IOException {
		try (Stream<Path> stream = Files.list(directory)) {
			Iterator<RegionKey> it = stream.map(Path::getFileName)
				.map(Path::toString)
				.map(RegionKey::new)
				.filter(keyProvider::isValid)
				.iterator();
			while (it.hasNext()) {
				RegionKey key = it.next();
				if (!keyProvider.isValid(key)) { continue; }
				consumer.accept(key, regionBuilder.create(keyProvider, key));
			}
		}
	}

	@Override public void flush() throws IOException {
	}

	@Override public void close() {
	}

	public static <K extends IKey> SimpleRegionProvider<K> createDefault(IKeyProvider keyProvider, Path directory, int sectorSize) {
		return new SimpleRegionProvider<>(keyProvider, directory, (keyProv, r) ->
			new Region.Builder<K>()
					.setDirectory(directory)
					.setRegionKey(r)
					.setKeyProvider(keyProv)
					.setSectorSize(sectorSize)
					.build(),
			(dir, key) -> Files.exists(dir.resolve(key.getRegionKey().getName()))
		);
	}

	@FunctionalInterface public interface IRegionFactory<K extends IKey> { IRegion<K> create(IKeyProvider keyProvider, RegionKey key) throws IOException; }

	@FunctionalInterface public interface IRegionExistsPredicate<K extends IKey> { boolean test(Path directory, K key) throws IOException; }
}
