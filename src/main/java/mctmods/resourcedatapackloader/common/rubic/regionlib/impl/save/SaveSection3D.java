package mctmods.resourcedatapackloader.common.rubic.regionlib.impl.save;

import mctmods.resourcedatapackloader.common.rubic.regionlib.api.region.interfaces.IRegionProvider;
import mctmods.resourcedatapackloader.common.rubic.regionlib.api.storage.SaveSection;
import mctmods.resourcedatapackloader.common.rubic.regionlib.impl.EntryLocation3D;

import java.util.List;

public class SaveSection3D extends SaveSection<EntryLocation3D> {
    public SaveSection3D(List<IRegionProvider<EntryLocation3D>> regionProviders) { super(regionProviders); }
}
