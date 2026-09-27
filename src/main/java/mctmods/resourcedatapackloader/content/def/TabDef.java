package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import java.util.List;
import java.util.Locale;

public record TabDef(Identifier key, String label, String icon, List<String> requires) {
    public Identifier id() {
        Identifier id = Identifier.tryBuild(key.getNamespace(), label.toLowerCase(Locale.ROOT));
        return id == null ? key : id;
    }
}
