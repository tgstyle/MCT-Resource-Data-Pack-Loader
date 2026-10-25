package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.core.MCTMixin;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.embeddedt.vintagefix.dynamicresources.ResourcePackHelper;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT) final class VintageFixAdapter {
    private VintageFixAdapter() {}

    static void register() {
        if (!MCTMixin.vintageFixPresent()) { return; }
        ResourcePackHelper.registerAdapter(RDPLResourcePack.class, (pack, filter) -> PackManager.get().rawPaths(pack.isOverriding()).stream().filter(filter).collect(Collectors.toList()).iterator());
    }
}
