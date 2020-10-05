package clientcommands.client.commands.arguments.generic;

import java.util.UUID;

import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.UUIDArgument;

import com.mojang.brigadier.context.CommandContext;

/**
 * Client Counterpart to {@link UUIDArgument}
 */
public class ClientUUIDArgument {

    public static <S> UUID getUUID(CommandContext<S> context, String name) {
        return context.getArgument(name, UUID.class);
    }

    public static UUIDArgument uuid() {
        return new UUIDArgument();
    }

}
