package mctmods.resourcedatapackloader.core.network;

import java.util.Collection;
import java.util.List;

public class UnlocksMessage extends AbstractStringListMessage {
    public final List<String> open = values;

    public UnlocksMessage() {}

    public UnlocksMessage(Collection<String> open) { super(open); }
}
