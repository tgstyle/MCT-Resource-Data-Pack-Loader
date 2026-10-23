package mctmods.resourcedatapackloader.content.def;

import java.util.List;
import javax.annotation.Nullable;

public record CurioSlotDef(String id, int size, @Nullable Integer order, String icon, boolean cosmetic, String dropRule, @Nullable Boolean renderToggle, List<String> entities) {}
