package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.common.syncdata.SyncMirror;
import mctmods.resourcedatapackloader.core.network.SyncDataMessage;

public final class ClientSyncData {
    private ClientSyncData() {}

    public static void receive(SyncDataMessage message) { SyncMirror.apply(message.full(), message.entries()); }

    public static void clear() { SyncMirror.reset(); }
}
