package mctmods.resourcedatapackloader.util;

import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.List;

public final class ConfigChunks {
    private final ModConfigSpec.BooleanValue saysCard;
    private final ModConfigSpec.ConfigValue<String> saysIcon;
    private final ModConfigSpec.ConfigValue<String> saysColor;
    private final ModConfigSpec.ConfigValue<String> saysImage;
    private final ModConfigSpec.ConfigValue<List<? extends String>> welcomeSays;
    private final ModConfigSpec.IntValue pregenOnNewWorld;
    private final ModConfigSpec.BooleanValue pregenToBorder;
    private final ModConfigSpec.IntValue pregenBorderLimit;
    private final ModConfigSpec.ConfigValue<List<? extends String>> pregenDimensions;
    private final ModConfigSpec.BooleanValue pregenAllDimensions;
    private final ModConfigSpec.ConfigValue<List<? extends String>> pregenDimensionsWhenEntered;
    private final ModConfigSpec.BooleanValue pregenResume;
    private final ModConfigSpec.IntValue pregenChunksInFlight;
    private final ModConfigSpec.ConfigValue<String> pregenRunningSays;
    private final ModConfigSpec.ConfigValue<String> pregenFinishedSays;
    private final ModConfigSpec.ConfigValue<String> pregenStoppedSays;
    private final ModConfigSpec.ConfigValue<String> pregenSpectatingSays;
    private final ModConfigSpec.ConfigValue<String> pregenLogo;
    private final ModConfigSpec.BooleanValue pregenBackup;
    private final ModConfigSpec.ConfigValue<String> pregenBackupSays;
    private final ModConfigSpec.ConfigValue<String> resetSays;
    private final ModConfigSpec.ConfigValue<String> resetSendsTo;
    private final ModConfigSpec.ConfigValue<String> resetRuns;
    private final ModConfigSpec.BooleanValue resetClearsEntities;
    private final ModConfigSpec.BooleanValue resetClearsScores;
    private final ModConfigSpec.BooleanValue resetClearsInventory;
    private final ModConfigSpec.BooleanValue resetClearsExperience;
    private final ModConfigSpec.IntValue spawnChunkRadius;
    private final ModConfigSpec.ConfigValue<List<? extends String>> spawnChunkRadii;

    ConfigChunks(ModConfigSpec.Builder builder) {
        builder.comment("What this mod says to players and how it shows it, and the land made before anybody plays").push("chunks");
        saysCard = builder.comment("Show the lines this mod says, the welcome, the land-making note a player joining mid-run gets and the end of the run (the running progress stays on the action bar), and the threat lines, as a card in the lower right corner instead of in chat. The card slides in, stays eight seconds and fades, and shows over an open screen too [Default=false]").define("saysCard", false);
        saysIcon = builder.comment("An item drawn on the card, e.g. minecraft:compass. Empty draws none [Default=]").define("saysIcon", "");
        saysColor = builder.comment("The card's background color as hex, e.g. 1E2630. Empty uses a dark slate [Default=]").define("saysColor", "");
        saysImage = builder.comment("A PNG from the pack's client assets stretched over the card as its background, e.g. rubyworld:textures/gui/card.png, drawn over the color. Empty draws none [Default=]").define("saysImage", "");
        welcomeSays = builder.comment("Welcome lines, shown in green on every login and after pregeneration. A bare entry is the line for everywhere; a dimension=message entry overrides it for that dimension and also greets every arrival there, e.g. minecraft:the_nether=Welcome to the Nether!. An empty message after the = mutes that dimension; an empty list shows nothing. Left at this default it speaks each player's language [Default=[Welcome to your World!]]").defineListAllowEmpty("welcomeSays", List.of(Config.WELCOME), () -> "", each -> each instanceof String);
        pregenOnNewWorld = builder.comment("How far around the spawn, in chunks, a world has its land made before anybody plays it. The game makes 12 chunks around the spawn on its own, so 12 is the floor and 0 means that floor rather than nothing: the ground the game was going to make anyway is adopted and lit in one organized pass instead of trickling in. Raise it to reach further than the game does [Default=0]").defineInRange("pregenOnNewWorld", 0, 0, 8192);
        pregenToBorder = builder.comment("Whether a new world has its land made out to its world border instead of a set number of chunks, centered on the border rather than the spawn. A world whose border was never moved in has no border to reach and is passed over [Default=false]").define("pregenToBorder", false);
        pregenBorderLimit = builder.comment("The furthest a border may reach, in chunks either way, before making land out to it is refused. This is here to stop a mistake running for weeks, not to be turned up, and a pack cannot set it. A square of 8192 holds 268 million chunks [Default=8192]").defineInRange("pregenBorderLimit", 8192, 1, 1875000);
        pregenDimensions = builder.comment("Which dimensions a new world has its land made in, by id, in the order given, one after another [Default=[minecraft:overworld]]").defineListAllowEmpty("pregenDimensions", List.of("minecraft:overworld"), () -> "", each -> each instanceof String);
        pregenAllDimensions = builder.comment("Make the land of every dimension the server holds, modded ones included, the overworld first and the rest in id order, instead of only those in pregenDimensions. Ones named in pregenDimensionsWhenEntered are still left for their first visitor [Default=false]").define("pregenAllDimensions", false);
        pregenDimensionsWhenEntered = builder.comment("Dimensions whose land is made not up front but the first time anybody sets foot in them, to the same reach, holding everybody the same way until it is done. One named here and in pregenDimensions is simply made up front [Default=[]]").defineListAllowEmpty("pregenDimensionsWhenEntered", List.of(), () -> "", each -> each instanceof String);
        pregenResume = builder.comment("Whether a run that was stopped or cut short picks up where it left off next time the world is loaded, rather than starting again [Default=false]").define("pregenResume", false);
        pregenChunksInFlight = builder.comment("How many chunks a land-making run asks the game for at once. More keeps the generation threads busier and the server less responsive to whoever is held watching [Default=32]").defineInRange("pregenChunksInFlight", 32, 1, 512);
        pregenRunningSays = builder.comment("The progress message players see while the world generates, where %d is the percentage and a second %s the dimension. Empty tells them nothing. Left at this default it speaks each player's language [Default=" + Config.PREGEN_RUNNING + "]").define("pregenRunningSays", Config.PREGEN_RUNNING);
        pregenFinishedSays = builder.comment("The message players see when generation finishes. Empty tells them nothing. Left at this default it speaks each player's language [Default=" + Config.PREGEN_FINISHED + "]").define("pregenFinishedSays", Config.PREGEN_FINISHED);
        pregenStoppedSays = builder.comment("The message players see when generation is stopped early. Empty tells them nothing. Left at this default it speaks each player's language [Default=" + Config.PREGEN_STOPPED + "]").define("pregenStoppedSays", Config.PREGEN_STOPPED);
        pregenSpectatingSays = builder.comment("The mid-screen message players see while held in spectator during world generation. Empty shows nothing. Left at this default it speaks each player's language [Default=" + Config.PREGEN_SPECTATING + "]").define("pregenSpectatingSays", Config.PREGEN_SPECTATING);
        pregenLogo = builder.comment("Where the logo stands when pregeneration finishes: left, center or right, above the mid-screen text. It is always shown; an unknown word is read as center [Default=center]").define("pregenLogo", "center");
        pregenBackup = builder.comment("Copy the world to a pristine backup once pregeneration finishes, while the players are still held. The copy is what a reset would restore, and a copy whose packs no longer match is thrown away and kept afresh [Default=false]").define("pregenBackup", false);
        pregenBackupSays = builder.comment("The mid-screen message players see while that backup is copied. Empty shows nothing [Default=Pack requested world backup]").define("pregenBackupSays", "Pack requested world backup");
        resetSays = builder.comment("The mid-screen line players are shown while /rdpl reset puts the map back. Empty resets quietly [Default=Pack requested map reset]").define("resetSays", "Pack requested map reset");
        resetSendsTo = builder.comment("Where players are put by a reset: spawn, a position as x,y,z, or dimension:x,y,z to send them into another world [Default=spawn]").define("resetSendsTo", "spawn");
        resetRuns = builder.comment("A function run after a reset has cleared the map, named namespace:path. Empty runs nothing [Default=empty]").define("resetRuns", "");
        resetClearsEntities = builder.comment("Remove every entity that is not a player when the map resets [Default=true]").define("resetClearsEntities", true);
        resetClearsScores = builder.comment("Set every objective the pack keeps back to nothing when the map resets, so a new match starts from zero. Teams themselves are kept [Default=true]").define("resetClearsScores", true);
        resetClearsInventory = builder.comment("Empty every player's inventory, armor and off hand included, when the map is reset [Default=false]").define("resetClearsInventory", false);
        resetClearsExperience = builder.comment("Set every player's experience back to level zero when the map is reset [Default=false]").define("resetClearsExperience", false);
        spawnChunkRadius = builder.comment("How far from the spawn point, in blocks, chunks are held loaded whether or not a player is there, rounded to whole chunks as (blocks + 8) / 16 each way, so 128 holds 8 chunks each way. As a world starts, the overworld prepares a square 4 chunks wider each way before the server is ready. 0 prepares and holds none, so the spawn area unloads like anywhere else [Default=128]").defineInRange("spawnChunkRadius", 128, 0, 1024);
        spawnChunkRadii = builder.comment("A radius for the overworld written as dimension=blocks, as in minecraft:overworld=64, which overrides spawnChunkRadius. Only the overworld has spawn chunks, so an entry for any other dimension changes nothing [Default=[]]").defineListAllowEmpty("spawnChunkRadii", List.of(), () -> "", each -> each instanceof String);
        builder.pop();
    }

    public int pregenOnNewWorld() { return Config.loaded() ? pregenOnNewWorld.get() : 0; }

    public boolean pregenToBorder() { return Config.loaded() && pregenToBorder.get(); }

    public int pregenBorderLimit() { return Config.loaded() ? pregenBorderLimit.get() : 8192; }

    public List<String> pregenDimensions() { return Config.loaded() ? List.copyOf(pregenDimensions.get()) : List.of("minecraft:overworld"); }

    public boolean pregenAllDimensions() { return Config.loaded() && pregenAllDimensions.get(); }

    public List<String> pregenDimensionsWhenEntered() { return Config.loaded() ? List.copyOf(pregenDimensionsWhenEntered.get()) : List.of(); }

    public boolean pregenResume() { return Config.loaded() && pregenResume.get(); }

    public int pregenChunksInFlight() { return Config.loaded() ? pregenChunksInFlight.get() : 32; }

    public String pregenRunningSays() { return Config.loaded() ? pregenRunningSays.get() : Config.PREGEN_RUNNING; }

    public String pregenFinishedSays() { return Config.loaded() ? pregenFinishedSays.get() : Config.PREGEN_FINISHED; }

    public String pregenStoppedSays() { return Config.loaded() ? pregenStoppedSays.get() : Config.PREGEN_STOPPED; }

    public String pregenSpectatingSays() { return Config.loaded() ? pregenSpectatingSays.get() : Config.PREGEN_SPECTATING; }

    public String pregenLogo() { return Config.loaded() ? pregenLogo.get() : "center"; }

    public boolean pregenBackup() { return Config.loaded() && pregenBackup.get(); }

    public String pregenBackupSays() { return Config.loaded() ? pregenBackupSays.get() : "Pack requested world backup"; }

    public String resetSays() { return Config.loaded() ? resetSays.get() : "Pack requested map reset"; }

    public String resetSendsTo() { return Config.loaded() ? resetSendsTo.get() : "spawn"; }

    public String resetRuns() { return Config.loaded() ? resetRuns.get() : ""; }

    public boolean resetClearsEntities() { return !Config.loaded() || resetClearsEntities.get(); }

    public boolean resetClearsScores() { return !Config.loaded() || resetClearsScores.get(); }

    public boolean resetClearsInventory() { return Config.loaded() && resetClearsInventory.get(); }

    public boolean resetClearsExperience() { return Config.loaded() && resetClearsExperience.get(); }

    public int spawnChunkRadius() { return Config.loaded() ? spawnChunkRadius.get() : 128; }

    public List<String> spawnChunkRadii() { return Config.loaded() ? List.copyOf(spawnChunkRadii.get()) : List.of(); }

    public boolean saysCard() { return Config.loaded() ? saysCard.get() : ConfigCore.flag("chunks.saysCard", false); }

    public String saysIcon() { return Config.loaded() ? saysIcon.get() : ConfigCore.text("chunks.saysIcon", ""); }

    public String saysColor() { return Config.loaded() ? saysColor.get() : ConfigCore.text("chunks.saysColor", ""); }

    public String saysImage() { return Config.loaded() ? saysImage.get() : ConfigCore.text("chunks.saysImage", ""); }

    public List<String> welcomeSays() { return Config.loaded() ? List.copyOf(welcomeSays.get()) : List.of(Config.WELCOME); }
}
