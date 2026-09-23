package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.server.packs.resources.IoSupplier;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.annotation.Nullable;

public final class SplashDark {
    private static final String LOGO = "/assets/resourcedatapackloader/textures/gui/splash.png";

    private SplashDark() {}

    @Nullable public static IoSupplier<InputStream> logo() {
        if (Config.tweaks.darkSplashOff()) { return null; }
        if (SplashDark.class.getResource(LOGO) == null) {
            ContentLog.LOGGER.warn("The pack loader's logo is not in its own jar, so the loading screen keeps the game's");
            return null;
        }
        ContentLog.LOGGER.info("The loading screen shows the pack loader's logo in place of the game's");
        return () -> {
            InputStream in = SplashDark.class.getResourceAsStream(LOGO);
            if (in == null) { throw new FileNotFoundException(LOGO); }
            return in;
        };
    }

    public static void apply() {
        Options options = Minecraft.getInstance().options;
        if (Config.tweaks.darkSplashOff()) {
            if (!options.darkMojangStudiosBackground().get()) { return; }
            options.darkMojangStudiosBackground().set(false);
            options.save();
            ContentLog.LOGGER.info("The loading screen is back to the game's own colors and logo from the next start on: the game's Monochrome Logo option is off again");
            return;
        }
        if (options.darkMojangStudiosBackground().get()) {
            ContentLog.LOGGER.info("The loading screen is dark already: the game's Monochrome Logo option is on");
            return;
        }
        options.darkMojangStudiosBackground().set(true);
        options.save();
        ContentLog.LOGGER.info("The loading screen is dark from the next start on, set through the game's own Monochrome Logo option");
    }
}
