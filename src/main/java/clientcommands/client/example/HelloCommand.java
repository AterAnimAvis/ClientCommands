package clientcommands.client.example;

import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.StringTextComponent;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import clientcommands.client.commands.ClientCommandSource;
import clientcommands.client.commands.arguments.selector.ClientEntityArgument;

import static clientcommands.client.commands.ClientCommands.argument;
import static clientcommands.client.commands.ClientCommands.command;
import static clientcommands.client.commands.ClientCommands.literal;

public class HelloCommand {

    private static final SuggestionProvider<ISuggestionProvider> provider =
        (context, builder) -> ISuggestionProvider.suggest(new String[] {"Client", "Minecraft", "World"}, builder);

    public static <T extends ArgumentBuilder<ISuggestionProvider, T>> void flat(
        ArgumentBuilder<ISuggestionProvider, T> root) {
        /* Branch */
        LiteralArgumentBuilder<ISuggestionProvider> hello = literal("hello");

        /* Arguments */
        RequiredArgumentBuilder<ISuggestionProvider, ?> entity = argument("entity", ClientEntityArgument.entity());
        RequiredArgumentBuilder<ISuggestionProvider, ?> place = argument("place", StringArgumentType.string());

        /* Suggestions */
        place.suggests(provider);

        /* Implementation */
        entity.executes(command(HelloCommand::hello));
        place.executes(command(HelloCommand::helloPlace));

        /* Layout */
        /* ... hello [player] (place) */
        root.then(hello.then(entity.then(place)));
    }

    public static int hello(CommandContext<ClientCommandSource> context) throws CommandSyntaxException {
        ClientCommandSource source = context.getSource();
        Entity entity = ClientEntityArgument.getEntity(context, "entity");

        source.sendFeedback(new StringTextComponent("Hello ")
                                .func_230529_a_/* appendSibling */(entity.getDisplayName())
                                .func_240702_b_/* appendText */("!"));

        return 1;
    }

    public static int helloPlace(CommandContext<ClientCommandSource> context) throws CommandSyntaxException {
        ClientCommandSource source = context.getSource();
        Entity entity = ClientEntityArgument.getEntity(context, "entity");
        String place = StringArgumentType.getString(context, "place");

        source.sendFeedback(new StringTextComponent("Hello ")
                                .func_230529_a_/* appendSibling */(entity.getDisplayName())
                                .func_240702_b_/* appendText */(" from " + place + "!"));

        return 1;
    }

}
