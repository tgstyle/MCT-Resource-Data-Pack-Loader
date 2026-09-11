package mctmods.resourcedatapackloader.content.compat;

import mctmods.resourcedatapackloader.content.item.ContentContainerItem;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class ContentCurios {
    public static final String MOD_ID = "curios";
    private static Boolean absent;
    @Nullable private static Api api;
    private static boolean broken;

    private ContentCurios() {}

    private record Api(Method inventoryOf, Method resolve, Method findCurios, Method stackOf) {}

    public static boolean missing() {
        if (absent == null) { absent = ModList.get() == null || !ModList.get().isLoaded(MOD_ID); }
        return absent;
    }

    public static List<ItemStack> worn(LivingEntity wearer) {
        List<ItemStack> found = new ArrayList<>();
        if (missing()) { return found; }
        Api held = bound();
        if (held == null) { return found; }
        try {
            Object lazy = held.inventoryOf().invoke(null, wearer);
            Object handler = lazy == null ? null : ((Optional<?>) held.resolve().invoke(lazy)).orElse(null);
            if (handler == null) { return found; }
            Predicate<ItemStack> ours = stack -> stack.getItem() instanceof ContentContainerItem;
            for (Object result : (List<?>) held.findCurios().invoke(handler, ours)) { found.add((ItemStack) held.stackOf().invoke(result)); }
        }
        catch (ReflectiveOperationException | RuntimeException ex) {
            broken = true;
            ContentLog.LOGGER.error("Curios is installed but its API did not answer as expected, so worn containers cannot be opened with the key", ex);
        }
        return found;
    }

    @Nullable private static Api bound() {
        if (api != null) { return api; }
        if (broken) { return null; }
        try {
            Class<?> hooks = Class.forName("top.theillusivec4.curios.api.CuriosApi");
            Class<?> handler = Class.forName("top.theillusivec4.curios.api.type.capability.ICuriosItemHandler");
            api = new Api(hooks.getMethod("getCuriosInventory", LivingEntity.class),
                    Class.forName("net.minecraftforge.common.util.LazyOptional").getMethod("resolve"),
                    handler.getMethod("findCurios", Predicate.class),
                    Class.forName("top.theillusivec4.curios.api.SlotResult").getMethod("stack"));
            return api;
        }
        catch (ReflectiveOperationException ex) {
            broken = true;
            ContentLog.LOGGER.error("Curios is installed but its API is not the shape this mod expects, so worn containers cannot be opened with the key", ex);
            return null;
        }
    }
}
