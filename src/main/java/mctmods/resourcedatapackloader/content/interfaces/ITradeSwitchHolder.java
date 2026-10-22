package mctmods.resourcedatapackloader.content.interfaces;

import mctmods.resourcedatapackloader.content.worldgen.ContentTradeSwitch;

import java.util.Map;

public interface ITradeSwitchHolder {
    Map<String, ContentTradeSwitch.Held> rdpl$held();

    boolean rdpl$own();
}
