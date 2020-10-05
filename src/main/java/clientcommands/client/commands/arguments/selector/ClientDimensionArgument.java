package clientcommands.client.commands.arguments.selector;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import clientcommands.client.commands.ClientCommandSource;

/**
 * Client Counterpart to {@link DimensionArgument}
 */
public class ClientDimensionArgument {

    private static final DynamicCommandExceptionType INVALID_DIMENSION =
        new DynamicCommandExceptionType((o) -> new TranslationTextComponent("argument.dimension.invalid", o));

    public static DimensionArgument dimension() {
        return new DimensionArgument();
    }

    public static <S> ResourceLocation getDimensionKey(CommandContext<S> context, String name) {
        return context.getArgument(name, ResourceLocation.class);
    }

    public static ClientWorld getDimensionArgument(CommandContext<ClientCommandSource> context, String name)
        throws CommandSyntaxException {
        ResourceLocation dimension = getDimensionKey(context, name);
        RegistryKey<World> key = RegistryKey
            .func_240903_a_/* getKey */(Registry.field_239699_ae_ /* dimension */, dimension);

        if (context.getSource().getWorld().func_234923_W_()/* getDimensionKey */.equals(key)) {
            return context.getSource().getWorld();
        }

        throw INVALID_DIMENSION.create(dimension);
    }

}
