package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.command.CommandShared;
import mctmods.resourcedatapackloader.common.ownership.OwnerTable;
import mctmods.resourcedatapackloader.common.scoring.ContentTeams;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.core.util.text.Lang;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public final class ClaimCommand {
    private static final String COMMAND = "rdplserver";
    private static final String CLAIM = "claim";
    private static final String UNCLAIM = "unclaim";
    private static final String LIST = "claims";
    private static final String FOR = "for";
    private static final String ALL = "all";
    private static final String DISABLED = "rdpl.claim.disabled";
    private static final String NO_OWNER = "rdpl.claim.noowner";
    public static final List<String> NAMES = Collections.unmodifiableList(Arrays.asList(CLAIM, UNCLAIM, LIST));

    private ClaimCommand() {}

    public static boolean handles(String name) { return NAMES.contains(name); }

    public static boolean listed() { return ClaimSettings.any() && ClaimSettings.allows(ClaimSettings.Method.COMMAND); }

    public static void run(MinecraftServer server, ICommandSender sender, String[] args, String usage) throws CommandException {
        String action = args[0];
        if (LIST.equals(action)) { list(server, sender, args, usage); }
        else if (!listed()) { throw new CommandException(Lang.tr(sender, DISABLED)); }
        else if (CLAIM.equals(action)) { claim(server, sender, args, usage); }
        else if (UNCLAIM.equals(action)) { unclaim(sender, args, usage); }
        else { throw new WrongUsageException(usage); }
    }

    public static List<String> complete(MinecraftServer server, ICommandSender sender, String[] args) {
        boolean op = sender.canUseCommand(ClaimSettings.opLevel(), COMMAND);
        String action = args[0];
        if (args.length == 2 && CLAIM.equals(action)) { return CommandBase.getListOfStringsMatchingLastWord(args, op ? Collections.singletonList(FOR) : Collections.emptyList()); }
        if (args.length == 2 && UNCLAIM.equals(action)) { return CommandBase.getListOfStringsMatchingLastWord(args, Collections.singletonList(ALL)); }
        if (args.length == 2 && LIST.equals(action) && op) { return CommandBase.getListOfStringsMatchingLastWord(args, owners(server)); }
        if (args.length == 3 && CLAIM.equals(action) && FOR.equals(args[1]) && op) { return CommandBase.getListOfStringsMatchingLastWord(args, owners(server)); }
        return Collections.emptyList();
    }

    private static List<String> owners(MinecraftServer server) {
        List<String> names = new ArrayList<>(ContentTeams.board(server).getTeamNames());
        names.addAll(Arrays.asList(server.getOnlinePlayerNames()));
        return names;
    }

    private static void claim(MinecraftServer server, ICommandSender sender, String[] args, String usage) throws CommandException {
        EntityPlayerMP player = CommandBase.getCommandSenderAsPlayer(sender);
        BlockPos at = player.getPosition();
        int chunkX = Coords.blockToCube(at.getX());
        int chunkZ = Coords.blockToCube(at.getZ());
        if (args.length == 3 && FOR.equals(args[1])) {
            if (!ClaimOwners.bypasses(player)) { throw new CommandException(Lang.tr(sender, "rdpl.command.notallowed")); }
            reply(sender, ClaimActions.claimFor(player.world, chunkX, chunkZ, ownerNamed(server, sender, args[2])));
        }
        else if (args.length == 1) { reply(sender, ClaimActions.claim(player, player.world, chunkX, chunkZ, 0)); }
        else if (args.length == 2) { reply(sender, ClaimActions.claimRadius(player, player.world, chunkX, chunkZ, CommandBase.parseInt(args[1], 0, ClaimSettings.commandRadius()))); }
        else { throw new WrongUsageException(usage); }
    }

    private static void unclaim(ICommandSender sender, String[] args, String usage) throws CommandException {
        EntityPlayerMP player = CommandBase.getCommandSenderAsPlayer(sender);
        BlockPos at = player.getPosition();
        int chunkX = Coords.blockToCube(at.getX());
        int chunkZ = Coords.blockToCube(at.getZ());
        if (args.length == 1) { reply(sender, ClaimActions.unclaim(player, player.world, chunkX, chunkZ)); }
        else if (args.length == 2 && ALL.equals(args[1])) { reply(sender, ClaimActions.unclaimAll(player)); }
        else if (args.length == 2) { reply(sender, ClaimActions.unclaimRadius(player, player.world, chunkX, chunkZ, CommandBase.parseInt(args[1], 0, ClaimSettings.commandRadius()))); }
        else { throw new WrongUsageException(usage); }
    }

    private static void list(MinecraftServer server, ICommandSender sender, String[] args, String usage) throws CommandException {
        int handle;
        if (args.length == 1) { handle = ClaimOwners.claimantOf(CommandBase.getCommandSenderAsPlayer(sender)); }
        else if (args.length == 2) {
            if (!sender.canUseCommand(ClaimSettings.opLevel(), COMMAND)) { throw new CommandException(Lang.tr(sender, "rdpl.command.notallowed")); }
            handle = ownerNamed(server, sender, args[1]);
        }
        else { throw new WrongUsageException(usage); }
        if (handle == 0) { throw new CommandException(Lang.tr(sender, ClaimActions.NO_CLAIMANT)); }
        for (ClaimActions.Result line : ClaimActions.listFor(server, handle)) { reply(sender, line); }
    }

    private static int ownerNamed(MinecraftServer server, ICommandSender sender, String name) throws CommandException {
        int handle = 0;
        if (ContentTeams.board(server).getTeamNames().contains(name)) { handle = OwnerTable.handleForTeam(server, name); }
        else {
            GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(name);
            if (profile != null) { handle = OwnerTable.handleForPlayer(server, profile.getId(), profile.getName()); }
        }
        if (handle == 0) { throw new CommandException(Lang.tr(sender, NO_OWNER, name)); }
        return handle;
    }

    private static void reply(ICommandSender sender, ClaimActions.Result result) throws CommandException {
        if (!result.ok) { throw new CommandException(Lang.tr(sender, result.key, result.args)); }
        say(sender, result);
    }

    public static void say(ICommandSender sender, ClaimActions.Result result) {
        CommandShared.send(sender, result.ok ? TextFormatting.GREEN : TextFormatting.RED, Lang.tr(sender, result.key, result.args));
    }
}
