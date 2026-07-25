package mctmods.resourcedatapackloader.core;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class MCTLateMixin implements ILateMixinLoader {

    @Override public List<String> getMixinConfigs() { return Collections.singletonList("mixins.resourcedatapackloader.jei.json"); }

    @Override public boolean shouldMixinConfigQueue(String mixinConfig) { return Loader.isModLoaded("jei"); }
}
