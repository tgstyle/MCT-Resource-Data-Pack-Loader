package mctmods.resourcedatapackloader.content.rubic.regionlib.impl.save;

import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IRegionProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.storage.SaveSection;
import mctmods.resourcedatapackloader.content.rubic.regionlib.impl.EntryLocation3D;
import mctmods.resourcedatapackloader.content.rubic.regionlib.lib.ExtRegion;
import mctmods.resourcedatapackloader.content.rubic.regionlib.lib.provider.SimpleRegionProvider;
import mctmods.resourcedatapackloader.content.rubic.server.chunkio.region.CachedRegionProvider;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SaveSection3D extends SaveSection<EntryLocation3D> {
    public SaveSection3D(List<IRegionProvider<EntryLocation3D>> regionProviders) { super(regionProviders); }

    public static SaveSection3D createAt(Path directory) {
        return new SaveSection3D(Arrays.asList(
                new CachedRegionProvider<>(
                        SimpleRegionProvider.createDefault(new EntryLocation3D.Provider(), directory, 512)
                ),
                new CachedRegionProvider<>(
                        new SimpleRegionProvider<>(new EntryLocation3D.Provider(), directory,
                                (keyProvider, regionKey) -> new ExtRegion<>(directory, Collections.emptyList(), keyProvider, regionKey),
                                (dir, key) -> Files.exists(dir.resolve(key.getRegionKey().getName() + ".ext"))
                        )
                )));
    }
}
