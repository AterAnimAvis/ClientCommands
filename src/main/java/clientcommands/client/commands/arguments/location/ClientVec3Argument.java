package clientcommands.client.commands.arguments.location;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.util.math.vector.Vector3d;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import clientcommands.client.commands.ClientCommandSource;
import clientcommands.client.commands.arguments.impl.ClientLocalLocationArgument;
import clientcommands.client.commands.arguments.impl.ClientLocationInput;

public class ClientVec3Argument implements ArgumentType<IClientLocation> {
    private static final Vec3Argument IMPL = Vec3Argument.vec3();

    private final boolean centerIntegers;

    public ClientVec3Argument(boolean centerIntegersIn) {
        this.centerIntegers = centerIntegersIn;
    }

    public static ClientVec3Argument vec3() {
        return new ClientVec3Argument(true);
    }

    public static ClientVec3Argument vec3(boolean centerIntegersIn) {
        return new ClientVec3Argument(centerIntegersIn);
    }

    public static Vector3d getVec3(CommandContext<ClientCommandSource> context, String name) {
        return context.getArgument(name, IClientLocation.class).getPosition(context.getSource());
    }

    public static IClientLocation getLocation(CommandContext<ClientCommandSource> context, String name) {
        return context.getArgument(name, IClientLocation.class);
    }

    public IClientLocation parse(StringReader reader) throws CommandSyntaxException {
        return (reader.canRead() && reader.peek() == '^' ? ClientLocalLocationArgument.parse(reader)
                                                         : ClientLocationInput.parseDouble(reader, centerIntegers));
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
                                                              SuggestionsBuilder builder) {

        return IMPL.listSuggestions(context, builder);
    }

    public Collection<String> getExamples() {
        return IMPL.getExamples();
    }

}
