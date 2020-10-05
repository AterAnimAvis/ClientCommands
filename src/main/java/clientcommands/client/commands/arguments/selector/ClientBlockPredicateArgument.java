package clientcommands.client.commands.arguments.selector;

import java.util.function.Predicate;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.util.CachedBlockInfo;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import clientcommands.client.commands.ClientCommandSource;

/**
 * Client Counterpart to {@link BlockPredicateArgument}
 */
public class ClientBlockPredicateArgument {

    public static BlockPredicateArgument blockPredicate() {
        return new BlockPredicateArgument();
    }

    public static <S> Predicate<CachedBlockInfo> getBlockPredicate(CommandContext<ClientCommandSource> context,
                                                                   String name)
        throws CommandSyntaxException {
        ClientWorld world = context.getSource().getWorld();
        return context.getArgument(name, BlockPredicateArgument.IResult.class).create(world.getTags());
    }

}
