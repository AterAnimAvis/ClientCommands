package clientcommands.client.commands.arguments.text;

import net.minecraft.command.arguments.ColorArgument;
import net.minecraft.util.text.TextFormatting;

import com.mojang.brigadier.context.CommandContext;

public class ClientColorArgument {

    public static ColorArgument color() {
        return ColorArgument.color();
    }

    public static <S> TextFormatting getColor(CommandContext<S> context, String name) {
        return context.getArgument(name, TextFormatting.class);
    }

}
