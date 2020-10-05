package clientcommands.client.commands.arguments.location;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import clientcommands.client.commands.ClientCommandSource;
import clientcommands.client.commands.arguments.impl.ClientLocalLocationArgument;
import clientcommands.client.commands.arguments.impl.ClientLocationInput;

public class ClientBlockPosArgument implements ArgumentType<IClientLocation> {
    private static final BlockPosArgument IMPL = BlockPosArgument.blockPos();

    public IClientLocation parse(StringReader reader) throws CommandSyntaxException {
        return (reader.canRead() && reader.peek() == '^' ? ClientLocalLocationArgument.parse(reader)
                                                         : ClientLocationInput.parseInt(reader));
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return IMPL.listSuggestions(context, builder);
    }

    public Collection<String> getExamples() {
        return IMPL.getExamples();
    }

    public static ClientBlockPosArgument blockPos() {
        return new ClientBlockPosArgument();
    }

    public static BlockPos getLoadedBlockPos(CommandContext<ClientCommandSource> context, String name)
        throws CommandSyntaxException {
        ClientCommandSource source = context.getSource();
        BlockPos pos = context.getArgument(name, IClientLocation.class).getBlockPos(context.getSource());

        World world = source.getWorld();
        if (!world.isBlockLoaded(pos)) throw BlockPosArgument.POS_UNLOADED.create();
        if (!ServerWorld.isValid(pos)) throw BlockPosArgument.POS_OUT_OF_WORLD.create();

        return pos;
    }

    public static BlockPos getBlockPos(CommandContext<ClientCommandSource> context, String name) {
        return context.getArgument(name, IClientLocation.class).getBlockPos(context.getSource());
    }

}
