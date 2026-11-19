package mctmods.resourcedatapackloader.common.command.custom;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.math.BigDecimal;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;

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

    ArgumentType<?> brigadier() {
        return switch (type) {
            case PLAYER -> EntityArgument.player();
            case INT -> IntegerArgumentType.integer();
            case NUMBER -> DoubleArgumentType.doubleArg();
            case TEXT -> StringArgumentType.greedyString();
            default -> StringArgumentType.string();
        };
    }

    String value(CommandContext<CommandSourceStack> context, String usage, boolean forScript) throws CommandSyntaxException {
        return switch (type) {
            case PLAYER -> EntityArgument.getPlayer(context, name).getPlainTextName();
            case INT -> String.valueOf(IntegerArgumentType.getInteger(context, name));
            case NUMBER -> BigDecimal.valueOf(DoubleArgumentType.getDouble(context, name)).toPlainString();
            default -> forScript ? StringArgumentType.getString(context, name) : refuse(StringArgumentType.getString(context, name), usage);
        };
    }

    String absent(CommandSourceStack source, String usage) throws CommandSyntaxException {
        if (!player()) { return fallback; }
        if (source.getPlayer() != null) { return source.getPlayer().getPlainTextName(); }
        throw wrong(usage);
    }

    static CommandSyntaxException wrong(String usage) { return new SimpleCommandExceptionType(Component.literal("Usage: " + usage)).create(); }

    private String refuse(String value, String usage) throws CommandSyntaxException {
        if (STRING.equals(type) && value.indexOf(' ') >= 0) { throw wrong(usage); }
        for (String token : value.split(" ", -1)) {
            if (token.startsWith("@")) { throw wrong(usage); }
        }
        for (int i = 0; i < value.length(); i++) {
            if (REFUSED.indexOf(value.charAt(i)) >= 0) { throw wrong(usage); }
        }
        return value;
    }
}
