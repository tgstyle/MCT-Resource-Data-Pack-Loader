package mctmods.resourcedatapackloader.content.rubic.regionlib.impl.save;

import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IRegionProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.storage.SaveSection;
import mctmods.resourcedatapackloader.content.rubic.regionlib.impl.EntryLocation3D;

import java.util.List;

public class SaveSection3D extends SaveSection<EntryLocation3D> {
    public SaveSection3D(List<IRegionProvider<EntryLocation3D>> regionProviders) { super(regionProviders); }
}
