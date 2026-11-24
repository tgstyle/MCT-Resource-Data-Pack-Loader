package mctmods.resourcedatapackloader.common.command.custom;

import java.math.BigDecimal;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

final class CommandArg {
    static final String PLAYER = "player";
    static final String INT = "int";
    static final String NUMBER = "number";
    static final String STRING = "string";
    static final String TEXT = "text";
    private static final String REFUSED = "{}\"\\\n\r";

    final String name;
    final String type;
    final boolean optional;
    private final String fallback;

    CommandArg(String name, String type, boolean optional, String fallback) {
        this.name = name;
        this.type = type;
        this.optional = optional;
        this.fallback = fallback;
    }

    boolean player() { return PLAYER.equals(type); }

    boolean text() { return TEXT.equals(type); }

    String value(MinecraftServer server, ICommandSender sender, String[] args, int index, String usage, boolean forScript) throws CommandException {
        if (index >= args.length) { return absent(sender, usage); }
        if (text()) { return joined(args, index, usage, forScript); }
        String raw = args[index];
        if (player()) { return CommandBase.getPlayer(server, sender, raw).getName(); }
        if (!forScript) { refuse(raw, usage); }
        if (INT.equals(type)) { return String.valueOf(CommandBase.parseInt(raw)); }
        if (NUMBER.equals(type)) { return BigDecimal.valueOf(CommandBase.parseDouble(raw)).toPlainString(); }
        return raw;
    }

    @Nullable Object typed(String value) {
        try {
            if (INT.equals(type)) { return value.isEmpty() ? null : Integer.valueOf(value); }
            if (NUMBER.equals(type)) { return value.isEmpty() ? null : Double.valueOf(value); }
        } catch (NumberFormatException e) { return null; }
        return value;
    }

    private String absent(ICommandSender sender, String usage) throws CommandException {
        if (!player()) { return fallback; }
        if (sender.getCommandSenderEntity() instanceof EntityPlayerMP) { return sender.getName(); }
        throw new WrongUsageException(usage);
    }

    private static String joined(String[] args, int index, String usage, boolean forScript) throws WrongUsageException {
        StringBuilder text = new StringBuilder();
        for (int i = index; i < args.length; i++) {
            if (!forScript) { refuse(args[i], usage); }
            if (i > index) { text.append(' '); }
            text.append(args[i]);
        }
        return text.toString();
    }

    private static void refuse(String value, String usage) throws WrongUsageException {
        if (value.startsWith("@")) { throw new WrongUsageException(usage); }
        for (int i = 0; i < value.length(); i++) {
            if (REFUSED.indexOf(value.charAt(i)) >= 0) { throw new WrongUsageException(usage); }
        }
    }
}
