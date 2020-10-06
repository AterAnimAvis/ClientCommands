package clientcommands.client.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import clientcommands.client.commands.impl.ClientCommandsImpl;
import clientcommands.common.Markers;

/**
 * Client Counterpart to {@link Commands}
 */
public class ClientCommands {

    /**
     * Creates a new argument. Intended to be imported statically. The benefit of this over the brigadier {@link
     * LiteralArgumentBuilder#literal(String)} method is that it is typed to {@link CommandSource}.
     */
    public static LiteralArgumentBuilder<ISuggestionProvider> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    /**
     * Creates a new argument. Intended to be imported statically. The benefit of this over the brigadier {@link
     * RequiredArgumentBuilder#argument} method is that it is typed to {@link CommandSource}.
     */
    public static <T> RequiredArgumentBuilder<ISuggestionProvider, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    /**
     * Wraps a {@code Command<ClientCommandSource>} so it can be used with a {@code
     * CommandContext<ISuggestionProvider>}
     */
    public static <S extends ISuggestionProvider> Command<S> command(Command<ClientCommandSource> command) {
        return context -> {
            if (context.getSource() instanceof ClientCommandSource) //noinspection unchecked
                return command.run((CommandContext<ClientCommandSource>) context);

            ClientCommandsImpl.LOGGER.warn(Markers.COMMAND, "Running a ClientCommand with an invalid ClientCommandSource");
            return 0;
        };
    }

}
