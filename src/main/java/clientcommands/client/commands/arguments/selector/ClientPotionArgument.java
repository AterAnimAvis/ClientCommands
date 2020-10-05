package clientcommands.client.commands.arguments.selector;

import net.minecraft.command.arguments.PotionArgument;
import net.minecraft.potion.Effect;

import com.mojang.brigadier.context.CommandContext;

public class ClientPotionArgument {

    public static PotionArgument mobEffect() {
        return new PotionArgument();
    }

    public static <S> Effect getMobEffect(CommandContext<S> context, String name) {
        return context.getArgument(name, Effect.class);
    }

}
