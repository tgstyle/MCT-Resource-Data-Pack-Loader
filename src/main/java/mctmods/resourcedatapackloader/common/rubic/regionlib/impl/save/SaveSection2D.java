package mctmods.resourcedatapackloader.common.rubic.regionlib.impl.save;

import mctmods.resourcedatapackloader.common.rubic.regionlib.api.region.interfaces.IRegionProvider;
import mctmods.resourcedatapackloader.common.rubic.regionlib.api.storage.SaveSection;
import mctmods.resourcedatapackloader.common.rubic.regionlib.impl.EntryLocation2D;

import java.util.List;

public class SaveSection2D extends SaveSection<EntryLocation2D> {
    public SaveSection2D(List<IRegionProvider<EntryLocation2D>> regionProviders) { super(regionProviders); }
}
