package clientcommands.client.commands.arguments.generic;

import net.minecraft.command.arguments.TimeArgument;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

public class ClientTimeArgument {

    public static TimeArgument timeArgument() {
        return new TimeArgument();
    }

    public static <S> int getTime(CommandContext<S> context, String name) {
        return IntegerArgumentType.getInteger(context, name);
    }

}
