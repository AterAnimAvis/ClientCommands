package clientcommands.client.commands.arguments.selector;

import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateInput;

import com.mojang.brigadier.context.CommandContext;

/**
 * Client Counterpart to {@link BlockStateArgument}
 */
public class ClientBlockStateArgument {

    public static BlockStateArgument blockState() {
        return new BlockStateArgument();
    }

    public static <S> BlockStateInput getBlockState(CommandContext<S> context, String name) {
        return context.getArgument(name, BlockStateInput.class);
    }

}
