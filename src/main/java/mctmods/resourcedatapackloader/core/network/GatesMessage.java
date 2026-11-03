package mctmods.resourcedatapackloader.core.network;

import java.util.Collection;
import java.util.List;

public class GatesMessage extends AbstractStringListMessage {
    public final List<String> gates = values;

    public GatesMessage() {}

    public GatesMessage(Collection<String> gates) { super(gates); }
}
