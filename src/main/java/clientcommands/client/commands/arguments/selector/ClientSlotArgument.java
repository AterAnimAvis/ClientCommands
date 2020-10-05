package clientcommands.client.commands.arguments.selector;

import net.minecraft.command.arguments.SlotArgument;

import com.mojang.brigadier.context.CommandContext;

public class ClientSlotArgument {

    public static SlotArgument slot() {
        return new SlotArgument();
    }

    public static <S> int getSlot(CommandContext<S> context, String name) {
        return context.getArgument(name, Integer.class);
    }

}
