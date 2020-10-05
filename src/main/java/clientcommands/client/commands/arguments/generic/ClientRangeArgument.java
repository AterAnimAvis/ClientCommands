package clientcommands.client.commands.arguments.generic;

import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.command.arguments.IRangeArgument;

import com.mojang.brigadier.context.CommandContext;

public class ClientRangeArgument {

    public static IRangeArgument.FloatRange floatRange() {
        return new IRangeArgument.FloatRange();
    }

    public static <S> MinMaxBounds.FloatBound getFloatBound(CommandContext<S> context, String name) {
        return context.getArgument(name, MinMaxBounds.FloatBound.class);
    }

    public static IRangeArgument.IntRange intRange() {
        return new IRangeArgument.IntRange();
    }

    public static <S> MinMaxBounds.IntBound getIntRange(CommandContext<S> context, String name) {
        return context.getArgument(name, MinMaxBounds.IntBound.class);
    }

}
