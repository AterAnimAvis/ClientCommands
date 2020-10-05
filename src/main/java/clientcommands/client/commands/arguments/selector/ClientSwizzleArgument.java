package clientcommands.client.commands.arguments.selector;

import java.util.EnumSet;

import net.minecraft.command.arguments.SwizzleArgument;
import net.minecraft.util.Direction;

import com.mojang.brigadier.context.CommandContext;

public class ClientSwizzleArgument {

    public static SwizzleArgument swizzle() {
        return new SwizzleArgument();
    }

    @SuppressWarnings("unchecked")
    public static <S> EnumSet<Direction.Axis> getSwizzle(CommandContext<S> context, String name) {
        return context.getArgument(name, EnumSet.class);
    }

}
