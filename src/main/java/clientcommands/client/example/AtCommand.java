package clientcommands.client.example;

import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import clientcommands.client.commands.ClientCommandSource;
import clientcommands.client.commands.arguments.location.ClientBlockPosArgument;

import static clientcommands.client.commands.ClientCommands.argument;
import static clientcommands.client.commands.ClientCommands.command;
import static clientcommands.client.commands.ClientCommands.literal;

public class AtCommand {

    public static LiteralArgumentBuilder<ISuggestionProvider> tree() {
        return literal("at").then(
            argument("position", ClientBlockPosArgument.blockPos())
                .executes(command(AtCommand::at))
        );
    }

    public static int at(CommandContext<ClientCommandSource> context) {
        ClientCommandSource source = context.getSource();
        BlockPos pos = ClientBlockPosArgument.getBlockPos(context, "position");

        source.sendFeedback(new StringTextComponent(source.getWorld().getBlockState(pos).toString()));

        return 1;
    }

}
