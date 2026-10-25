package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.ContentServer;
import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class CreateWorldPack {
    private CreateWorldPack() {}

    @SubscribeEvent public static void showPackChoices(GuiScreenEvent.InitGuiEvent.Pre event) {
        if (!(event.getGui() instanceof GuiCreateWorld)) { return; }
        GuiCreateWorld screen = (GuiCreateWorld) event.getGui();
        String named = ContentTerrain.worldName();
        String seed = ContentTerrain.worldSeed();
        String fresh = I18n.format("selectWorld.newWorld");
        ContentLog.LOGGER.debug("The screen for making a world opened. A pack asks for the name '{}', the seed '{}' and the game mode '{}'. The box says '{}' and the game calls a new world '{}', so the name {} be filled in",
                named, seed, ContentServer.worldGameMode(), screen.worldName, fresh, screen.worldName.equals(fresh) ? "will" : "will not");
        if (!named.isEmpty() && screen.worldName.equals(fresh)) { screen.worldName = named; }
        if (!seed.isEmpty()) { screen.worldSeed = seed; }
        GameType asked = ContentServer.gameModeFrom(ContentServer.worldGameMode());
        if (asked != GameType.SURVIVAL && asked != GameType.CREATIVE) { return; }
        boolean hardcore = ContentServer.hardcoreAsked();
        screen.gameMode = hardcore ? "hardcore" : asked.getName();
        screen.hardCoreMode = hardcore;
        if (screen.allowCheatsWasSetByUser) { return; }
        screen.allowCheats = asked == GameType.CREATIVE;
    }

    @SubscribeEvent public static void lockSeed(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.getGui() instanceof GuiCreateWorld)) { return; }
        GuiCreateWorld screen = (GuiCreateWorld) event.getGui();
        if (ContentTerrain.worldSeed().isEmpty() || screen.worldSeedField == null) { return; }
        screen.worldSeedField.setEnabled(false);
    }
}
