package mctmods.resourcedatapackloader.core.rhino.regexp;

import mctmods.resourcedatapackloader.core.rhino.RegExpLoader;
import mctmods.resourcedatapackloader.core.rhino.RegExpProxy;

/** This class loads the default RegExp implementation. */
public class RegExpLoaderImpl implements RegExpLoader {
    @Override
    public RegExpProxy newProxy() {
        return new RegExpImpl();
    }
}
