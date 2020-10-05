package clientcommands.client.commands.arguments.location;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import net.minecraft.command.arguments.ColumnPosArgument;
import net.minecraft.command.arguments.LocationPart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColumnPos;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import clientcommands.client.commands.ClientCommandSource;
import clientcommands.client.commands.arguments.impl.ClientLocationInput;

public class ClientColumnPosArgument implements ArgumentType<IClientLocation> {
    private static final ColumnPosArgument IMPL = ColumnPosArgument.columnPos();

    public IClientLocation parse(StringReader reader) throws CommandSyntaxException {
        int initialPosition = reader.getCursor();

        if (!reader.canRead()) throw ColumnPosArgument.INCOMPLETE_EXCEPTION.createWithContext(reader);

        LocationPart part0 = LocationPart.parseInt(reader);
        if (!reader.canRead() || reader.peek() != ' ') {
            reader.setCursor(initialPosition);
            throw ColumnPosArgument.INCOMPLETE_EXCEPTION.createWithContext(reader);
        }
        reader.skip();

        LocationPart part1 = LocationPart.parseInt(reader);
        return new ClientLocationInput(part0, new LocationPart(true, 0.0D), part1);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return IMPL.listSuggestions(context, builder);
    }

    public Collection<String> getExamples() {
        return IMPL.getExamples();
    }

    public static ClientColumnPosArgument columnPos() {
        return new ClientColumnPosArgument();
    }

    public static ColumnPos fromBlockPos(CommandContext<ClientCommandSource> context, String name) {
        BlockPos blockpos = ClientBlockPosArgument.getBlockPos(context, name);
        return new ColumnPos(blockpos.getX(), blockpos.getZ());
    }

}
