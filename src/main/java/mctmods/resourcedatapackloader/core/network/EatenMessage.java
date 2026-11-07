package mctmods.resourcedatapackloader.core.network;

import java.util.Collection;
import java.util.List;

public class EatenMessage extends AbstractStringListMessage {
    public final List<String> eaten = values;

    public EatenMessage() {}

    public EatenMessage(Collection<String> eaten) { super(eaten); }
}
