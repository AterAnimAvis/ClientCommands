package clientcommands.client.commands.arguments.selector;

import net.minecraft.command.arguments.EnchantmentArgument;
import net.minecraft.enchantment.Enchantment;

import com.mojang.brigadier.context.CommandContext;

public class ClientEnchantmentArgument {

    public static EnchantmentArgument enchantment() {
        return new EnchantmentArgument();
    }

    public static <S> Enchantment getEnchantment(CommandContext<S> context, String name) {
        return context.getArgument(name, Enchantment.class);
    }

}
