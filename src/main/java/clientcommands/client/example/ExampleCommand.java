package clientcommands.client.example;

import net.minecraft.command.ISuggestionProvider;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import clientcommands.client.commands.ClientCommands;
import clientcommands.client.commands.arguments.selector.ClientEntityArgument;

public class ExampleCommand {

    private static final SuggestionProvider<ISuggestionProvider> provider = (context, builder) ->
        ISuggestionProvider.suggest(new String[] {"Client", "Minecraft", "World"}, builder);

    public static void register(CommandDispatcher<ISuggestionProvider> dispatcher) {
        dispatcher.register(example());
    }

    public static LiteralArgumentBuilder<ISuggestionProvider> example() {
        // @formatter:off
        return ClientCommands.literal("hello")
                             .then(ClientCommands.argument("entity", ClientEntityArgument.entity())
                                   .executes(ClientCommands.command(HelloCommand::hello))
                                   .then(ClientCommands.argument("place", StringArgumentType.string())
                                         .suggests(provider)
                                         .executes(ClientCommands.command(HelloCommand::helloPlace))
                                   )
        );
        // @formatter:on
    }

}
