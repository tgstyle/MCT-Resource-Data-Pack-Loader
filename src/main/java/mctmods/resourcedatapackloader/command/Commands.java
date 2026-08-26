package mctmods.resourcedatapackloader.command;

import static mctmods.resourcedatapackloader.command.CommandShared.config;
import static mctmods.resourcedatapackloader.command.CommandShared.send;
import static mctmods.resourcedatapackloader.command.CommandShared.unused;
import static mctmods.resourcedatapackloader.command.CommandShared.elapsed;
import static mctmods.resourcedatapackloader.command.CommandShared.biomeNames;
import static mctmods.resourcedatapackloader.command.CommandShared.biomeHere;
import static mctmods.resourcedatapackloader.command.CommandShared.biomeList;
import mctmods.resourcedatapackloader.content.ContentOverrides;
import mctmods.resourcedatapackloader.content.ContentPixelMaps;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.pack.RDPLPack;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ReloadRequirements;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT) public class Commands extends CommandBase {
    private static final List<String> SUBCOMMANDS = Arrays.asList("reload", "list", "which", "unused", "biome", "config", "pixelmap");
    private static final List<String> CONFIG_SUBCOMMANDS = Arrays.asList("unused", "prune");
    private static final Map<String, IResourceType> GROUPS = groups();
    private static final List<String> BIOME_SUBCOMMANDS = Arrays.asList("list", "here", "find");
    private static final List<String> FORWARDED = Arrays.asList("oregen", "generators", "gate", "dimensions", "pregen", "intro", "goto");

    private static Map<String, IResourceType> groups() {
        Map<String, IResourceType> map = new LinkedHashMap<>();
        map.put("textures", VanillaResourceType.TEXTURES);
        map.put("models", VanillaResourceType.MODELS);
        map.put("languages", VanillaResourceType.LANGUAGES);
        map.put("sounds", VanillaResourceType.SOUNDS);
        map.put("shaders", VanillaResourceType.SHADERS);
        return Collections.unmodifiableMap(map);
    }

    @Override @Nonnull public String getName() { return "rdpl"; }

    @Override @Nonnull public String getUsage(@Nonnull ICommandSender sender) { return Lang.tr(sender, "rdpl.command.usage", String.join("|", GROUPS.keySet())); }

    @Override public int getRequiredPermissionLevel() { return 0; }

    @Override public boolean checkPermission(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender) { return true; }

    @Override @Nonnull public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            List<String> offered = new ArrayList<>(SUBCOMMANDS);
            offered.addAll(FORWARDED);
            return getListOfStringsMatchingLastWord(args, offered);
        }
        if (args.length == 2 && "reload".equals(args[0])) { return getListOfStringsMatchingLastWord(args, new ArrayList<>(GROUPS.keySet())); }
        if (args.length == 2 && "biome".equals(args[0])) { return getListOfStringsMatchingLastWord(args, BIOME_SUBCOMMANDS); }
        if (args.length == 2 && "config".equals(args[0])) { return getListOfStringsMatchingLastWord(args, CONFIG_SUBCOMMANDS); }
        if (args.length == 3 && "biome".equals(args[0]) && "find".equals(args[1])) { return getListOfStringsMatchingLastWord(args, biomeNames()); }
        return Collections.emptyList();
    }

    @Override public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        ContentLog.LOGGER.debug("{} ran /{} {}", sender.getName(), getName(), String.join(" ", args));
        if (args.length == 1 && "reload".equals(args[0])) { reloadAll(sender); }
        else if (args.length == 2 && "reload".equals(args[0])) { reloadGroup(sender, args[1]); }
        else if (args.length == 1 && "list".equals(args[0])) { list(sender); }
        else if (args.length == 2 && "which".equals(args[0])) { which(sender, args[1]); }
        else if (args.length == 1 && "unused".equals(args[0])) { unused(sender, "rdpl.command.unusednote"); }
        else if (args.length > 0 && "biome".equals(args[0])) { biome(sender, args); }
        else if (args.length == 2 && "config".equals(args[0])) { config(sender, args[1], getUsage(sender), "rdpl.command.config.note"); }
        else if (args.length == 2 && "pixelmap".equals(args[0])) { pixelmap(sender, args[1]); }
        else if (args.length > 0 && FORWARDED.contains(args[0])) { forward(args); }
        else { throw new WrongUsageException(getUsage(sender)); }
    }

    private void reloadAll(ICommandSender sender) throws CommandException {
        Path root = PackManager.get().getRoot();
        if (root == null) { throw new CommandException(Lang.tr(sender, "rdpl.command.noroot")); }
        long start = System.currentTimeMillis();
        PackManager.get().scan(root);
        PackManager.get().report();
        ContentOverrides.reload();
        FMLClientHandler.instance().refreshResources(ReloadRequirements.all());
        MinecraftServer integrated = Minecraft.getMinecraft().getIntegratedServer();
        if (integrated != null) { integrated.reload(); }
        int packs = PackManager.get().getPacks().size();
        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.reloaded", packs, elapsed(start)));
        if (integrated == null) { send(sender, TextFormatting.GRAY, Lang.tr(sender, "rdpl.command.clientonly")); }
        else { send(sender, TextFormatting.GRAY, Lang.tr(sender, "rdpl.command.recipes")); }
    }

    private void reloadGroup(ICommandSender sender, String name) throws CommandException {
        IResourceType type = GROUPS.get(name.toLowerCase(Locale.ROOT));
        if (type == null) { throw new CommandException(Lang.tr(sender, "rdpl.command.nogroup", name, String.join(", ", GROUPS.keySet()))); }
        long start = System.currentTimeMillis();
        FMLClientHandler.instance().refreshResources(type);
        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.reloadedgroup", name.toLowerCase(Locale.ROOT), elapsed(start)));
        send(sender, TextFormatting.GRAY, Lang.tr(sender, "rdpl.command.groupnote"));
    }

    private void list(ICommandSender sender) {
        List<RDPLPack> packs = PackManager.get().getPacks();
        if (packs.isEmpty()) {
            send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.nopacks", PackManager.get().getRoot()));
            return;
        }
        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.packs", packs.size()));
        for (RDPLPack pack : packs) {
            String priority = pack.getPriority() >= 0 ? " [" + pack.getPriority() + "]" : "";
            String tier = pack.isOverriding() ? Lang.tr(sender, "rdpl.command.overriding") : "";
            String detail = "files=" + pack.getFileCount()
                    + "\nnamespaces=" + pack.getNamespaces()
                    + "\nadvancements=" + pack.count(PackManager.ADVANCEMENTS, PackManager.JSON)
                    + " loot_tables=" + pack.count(PackManager.LOOT_TABLES, PackManager.JSON)
                    + " recipes=" + pack.count(PackManager.RECIPES, PackManager.JSON)
                    + "\nfunctions=" + pack.count(PackManager.FUNCTIONS, PackManager.MCFUNCTION)
                    + " remaps=" + pack.count(PackManager.REGISTRY_REMAP, PackManager.JSON)
                    + "\n" + Lang.tr(sender, "rdpl.command.clickhint");
            ITextComponent line = new TextComponentString("  " + pack.getName() + priority + tier);
            line.getStyle().setColor(pack.isOverriding() ? TextFormatting.AQUA : TextFormatting.WHITE);
            line.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(detail)));
            line.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rdpl which " + firstNamespace(pack) + ":"));
            send(sender, line, "  " + pack.getName() + priority + tier + " " + detail.replace('\n', ' '));
        }
    }

    private static String firstNamespace(RDPLPack pack) {
        for (String namespace : pack.getNamespaces()) { return namespace; }
        return "minecraft";
    }

    private static void forward(String[] args) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) { return; }
        player.sendChatMessage("/rdplserver " + String.join(" ", args));
    }

    private void pixelmap(ICommandSender sender, String target) throws CommandException {
        int colon = target.indexOf(':');
        if (colon < 1) { throw new CommandException(Lang.tr(sender, "rdpl.command.pixelmapname")); }
        String namespace = target.substring(0, colon);
        String path = target.substring(colon + 1);
        if (!path.startsWith("textures/")) { path = "textures/" + path; }
        if (!path.endsWith(ContentPixelMaps.PNG)) { path = path + ContentPixelMaps.PNG; }
        ContentPixelMaps.Resolved resolved = ContentPixelMaps.resolve(namespace, path, false);
        if (resolved == null) { resolved = ContentPixelMaps.resolve(namespace, path, true); }
        if (resolved == null) { throw new CommandException(Lang.tr(sender, "rdpl.command.pixelmapnone", namespace + ":" + path)); }
        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.pixelmapis", namespace + ":" + path, resolved.size[0], resolved.size[1]));
        for (String held : resolved.chain) { send(sender, TextFormatting.GRAY, Lang.tr(sender, "rdpl.command.pixelmapfrom", held)); }
        send(sender, TextFormatting.GRAY, Lang.tr(sender, "rdpl.command.pixelmaprows", resolved.rowsFrom));
        for (Map.Entry<String, String> entry : resolved.palette.entrySet()) {
            String note = resolved.notes.get(entry.getKey());
            send(sender, TextFormatting.WHITE, Lang.tr(sender, "rdpl.command.pixelmapkey",
                    entry.getKey(), entry.getValue(), resolved.used(entry.getKey()),
                    resolved.from.getOrDefault(entry.getKey(), "?"), note == null ? "" : note));
        }
    }

    private void which(ICommandSender sender, String target) {
        int colon = target.indexOf(':');
        String namespace = colon < 0 ? "minecraft" : target.substring(0, colon);
        String path = colon < 0 ? target : target.substring(colon + 1);
        List<RDPLPack> holders = PackManager.get().holders(namespace, path);
        if (holders.isEmpty()) {
            send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.unprovided", namespace + ":" + path));
            return;
        }
        RDPLPack winner = holders.get(holders.size() - 1);
        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.provided", namespace + ":" + path, winner.getName(), winner.isOverriding() ? Lang.tr(sender, "rdpl.command.overriding") : ""));
        send(sender, TextFormatting.GRAY, Lang.tr(sender, "rdpl.command.providednote"));
        for (int i = holders.size() - 2; i >= 0; i--) { send(sender, TextFormatting.GRAY, Lang.tr(sender, "rdpl.command.shadows", holders.get(i).getName())); }
    }

    private void biome(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1 || "list".equals(args[1])) { biomeList(sender, args.length > 2 && "all".equals(args[2])); }
        else if ("here".equals(args[1])) { biomeHere(sender); }
        else if (args.length >= 3 && "find".equals(args[1])) { forward(args); }
        else { throw new WrongUsageException(getUsage(sender)); }
    }

}
