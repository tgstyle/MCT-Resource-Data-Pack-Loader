package mctmods.resourcedatapackloader.common.worldgen.structure.city.road;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public interface IDrawnRoad {
    int rdpl$bulbEnds();

    void rdpl$bulbEnds(int ends);

    int[] rdpl$lifts();

    void rdpl$lifts(int[] lifts);

    void rdpl$keys(@Nullable JsonObject keys);
}
