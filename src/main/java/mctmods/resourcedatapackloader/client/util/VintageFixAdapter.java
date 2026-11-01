package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.core.MCTMixin;

import java.util.stream.Collectors;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.embeddedt.vintagefix.dynamicresources.ResourcePackHelper;

@SideOnly(Side.CLIENT) public final class VintageFixAdapter {
    private VintageFixAdapter() {}

    public static void register() {
        if (!MCTMixin.vintageFixPresent()) { return; }
        ResourcePackHelper.registerAdapter(RDPLResourcePack.class, (pack, filter) -> PackManager.get().rawPaths(pack.isOverriding()).stream().filter(filter).collect(Collectors.toList()).iterator());
    }
}
