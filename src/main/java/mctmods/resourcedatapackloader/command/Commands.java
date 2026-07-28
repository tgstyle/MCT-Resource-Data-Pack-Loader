package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.core.MCTMixin;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.pack.RDPLPack;

import net.minecraft.client.Minecraft;
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

@SideOnly(Side.CLIENT)
public class Commands extends CommandBase {
    private static final List<String> SUBCOMMANDS = Arrays.asList("reload", "list", "which", "unused");
    private static final Map<String, IResourceType> GROUPS = groups();

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

    @Override @Nonnull public String getUsage(@Nonnull ICommandSender sender) { return "/rdpl <reload [" + String.join("|", GROUPS.keySet()) + "]|list|which <namespace:path>|unused>"; }

    @Override public int getRequiredPermissionLevel() { return 0; }

    @Override public boolean checkPermission(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender) { return true; }

    @Override @Nonnull public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) { return getListOfStringsMatchingLastWord(args, SUBCOMMANDS); }
        if (args.length == 2 && "reload".equals(args[0])) { return getListOfStringsMatchingLastWord(args, new ArrayList<>(GROUPS.keySet())); }
        return Collections.emptyList();
    }

    @Override public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        MCTMixin.LOGGER.info("{} ran /{} {}", sender.getName(), getName(), String.join(" ", args));
        if (args.length == 1 && "reload".equals(args[0])) { reloadAll(sender); }
        else if (args.length == 2 && "reload".equals(args[0])) { reloadGroup(sender, args[1]); }
        else if (args.length == 1 && "list".equals(args[0])) { list(sender); }
        else if (args.length == 2 && "which".equals(args[0])) { which(sender, args[1]); }
        else if (args.length == 1 && "unused".equals(args[0])) { unused(sender); }
        else { throw new WrongUsageException(getUsage(sender)); }
    }

    private void reloadAll(ICommandSender sender) throws CommandException {
        Path root = PackManager.get().getRoot();
        if (root == null) { throw new CommandException("Pack root is not known yet"); }
        long start = System.currentTimeMillis();
        PackManager.get().scan(root);
        PackManager.get().report();
        FMLClientHandler.instance().refreshResources(ReloadRequirements.all());
        MinecraftServer integrated = Minecraft.getMinecraft().getIntegratedServer();
        if (integrated != null) { integrated.reload(); }
        int packs = PackManager.get().getPacks().size();
        send(sender, TextFormatting.GREEN, "Rescanned " + packs + " pack(s) and reloaded resources in " + elapsed(start));
        if (integrated == null) { send(sender, TextFormatting.GRAY, "Only your own resources were reloaded. On a server, an operator runs /rdplserver reload"); }
        else { send(sender, TextFormatting.GRAY, "Recipes are not reloadable and still need a restart"); }
    }

    private void reloadGroup(ICommandSender sender, String name) throws CommandException {
        IResourceType type = GROUPS.get(name.toLowerCase(Locale.ROOT));
        if (type == null) { throw new CommandException("No such group '" + name + "', try one of " + String.join(", ", GROUPS.keySet())); }
        long start = System.currentTimeMillis();
        FMLClientHandler.instance().refreshResources(type);
        send(sender, TextFormatting.GREEN, "Reloaded " + name.toLowerCase(Locale.ROOT) + " in " + elapsed(start));
        send(sender, TextFormatting.GRAY, "Files added or removed since startup need /rdpl reload with no group");
    }

    private void list(ICommandSender sender) {
        List<RDPLPack> packs = PackManager.get().getPacks();
        if (packs.isEmpty()) {
            send(sender, TextFormatting.YELLOW, "No packs loaded from " + PackManager.get().getRoot());
            return;
        }
        send(sender, TextFormatting.GREEN, packs.size() + " pack(s), lowest priority first:");
        for (RDPLPack pack : packs) {
            String priority = pack.getPriority() >= 0 ? " [" + pack.getPriority() + "]" : "";
            String tier = pack.isOverriding() ? " (overriding)" : "";
            String detail = "files=" + pack.getFileCount()
                    + "\nnamespaces=" + pack.getNamespaces()
                    + "\nadvancements=" + pack.count(PackManager.ADVANCEMENTS, PackManager.JSON)
                    + " loot_tables=" + pack.count(PackManager.LOOT_TABLES, PackManager.JSON)
                    + " recipes=" + pack.count(PackManager.RECIPES, PackManager.JSON)
                    + "\nfunctions=" + pack.count(PackManager.FUNCTIONS, PackManager.MCFUNCTION)
                    + " remaps=" + pack.count(PackManager.REGISTRY_REMAP, PackManager.JSON)
                    + "\nClick to look up a file in this namespace";
            ITextComponent line = new TextComponentString("  " + pack.getName() + priority + tier);
            line.getStyle().setColor(pack.isOverriding() ? TextFormatting.AQUA : TextFormatting.WHITE);
            line.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(detail)));
            line.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rdpl which " + firstNamespace(pack) + ":"));
            sender.sendMessage(line);
        }
    }

    private void unused(ICommandSender sender) {
        List<String> unused = PackManager.get().findUnused();
        if (unused.isEmpty()) {
            send(sender, TextFormatting.GREEN, "Every file in your packs has been asked for at least once");
            return;
        }
        send(sender, TextFormatting.YELLOW, unused.size() + " file(s) have not been asked for:");
        for (String entry : unused) { MCTMixin.LOGGER.warn("  {}", entry); }
        send(sender, TextFormatting.GRAY, "Some only load when they are needed, such as other languages, so check the paths rather than deleting them");
    }

    private static String firstNamespace(RDPLPack pack) {
        for (String namespace : pack.getNamespaces()) { return namespace; }
        return "minecraft";
    }

    private void which(ICommandSender sender, String target) {
        int colon = target.indexOf(':');
        String namespace = colon < 0 ? "minecraft" : target.substring(0, colon);
        String path = colon < 0 ? target : target.substring(colon + 1);
        List<RDPLPack> holders = PackManager.get().holders(namespace, path);
        if (holders.isEmpty()) {
            send(sender, TextFormatting.YELLOW, namespace + ":" + path + " is not provided by any pack, Minecraft or a mod will serve it");
            return;
        }
        RDPLPack winner = holders.get(holders.size() - 1);
        send(sender, TextFormatting.GREEN, namespace + ":" + path + " is provided by '" + winner.getName() + "'" + (winner.isOverriding() ? " (overriding)" : ""));
        send(sender, TextFormatting.GRAY, "This is what your packs offer, not proof the game asked for it. Use /rdpl unused to see what is never requested");
        for (int i = holders.size() - 2; i >= 0; i--) {
            send(sender, TextFormatting.GRAY, "  shadows '" + holders.get(i).getName() + "'");
        }
    }

    private static String elapsed(long start) {
        long time = System.currentTimeMillis() - start;
        return time < 1000L ? (time + "ms") : String.format("%.02fs", time / 1000D);
    }

    private static void send(ICommandSender sender, TextFormatting colour, String message) {
        sender.sendMessage(new TextComponentString(colour + message));
        MCTMixin.LOGGER.info("  {}", message);
    }
}
