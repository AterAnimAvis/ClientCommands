package clientcommands.client.commands.arguments.text;

import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.util.text.ITextComponent;

import com.mojang.brigadier.context.CommandContext;

public class ClientComponentArgument {

    public static ComponentArgument component() {
        return ComponentArgument.component();
    }

    public static <S> ITextComponent getComponent(CommandContext<S> context, String name) {
        return context.getArgument(name, ITextComponent.class);
    }

}
