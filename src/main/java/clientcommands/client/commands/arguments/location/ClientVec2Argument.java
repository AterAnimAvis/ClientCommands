package clientcommands.client.commands.arguments.location;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import net.minecraft.command.arguments.LocationPart;
import net.minecraft.command.arguments.Vec2Argument;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import clientcommands.client.commands.ClientCommandSource;
import clientcommands.client.commands.arguments.impl.ClientLocationInput;

public class ClientVec2Argument implements ArgumentType<IClientLocation> {
    private static final Vec2Argument IMPL = Vec2Argument.vec2();

    private final boolean centerIntegers;

    public ClientVec2Argument(boolean centerIntegersIn) {
        this.centerIntegers = centerIntegersIn;
    }

    public static ClientVec2Argument vec2() {
        return new ClientVec2Argument(true);
    }

    public static Vector2f getVec2f(CommandContext<ClientCommandSource> context, String name) {
        Vector3d vec3d = context.getArgument(name, IClientLocation.class).getPosition(context.getSource());
        return new Vector2f((float) vec3d.x, (float) vec3d.z);
    }

    public IClientLocation parse(StringReader reader) throws CommandSyntaxException {
        int initialPosition = reader.getCursor();

        if (!reader.canRead()) throw Vec2Argument.VEC2_INCOMPLETE.createWithContext(reader);

        LocationPart part0 = LocationPart.parseDouble(reader, this.centerIntegers);
        if (!reader.canRead() || reader.peek() != ' ') {
            reader.setCursor(initialPosition);
            throw Vec2Argument.VEC2_INCOMPLETE.createWithContext(reader);
        }
        reader.skip();

        LocationPart part1 = LocationPart.parseDouble(reader, this.centerIntegers);
        return new ClientLocationInput(part0, new LocationPart(true, 0.0D), part1);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return IMPL.listSuggestions(context, builder);
    }

    public Collection<String> getExamples() {
        return IMPL.getExamples();
    }

}
