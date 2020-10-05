package clientcommands.client;

import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import clientcommands.client.commands.ClientCommands;
import clientcommands.client.event.RegisterClientCommandsEvent;
import clientcommands.client.example.AtCommand;
import clientcommands.client.example.ExampleCommand;
import clientcommands.client.example.HelloCommand;
import clientcommands.client.example.TestCommand;
import clientcommands.common.CommonProxy;

public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        super.init();

        /* Runtime Events */
        registerEventListener(this);
    }

    @SubscribeEvent
    public void registerClientCommands(RegisterClientCommandsEvent event) {
        // Style 1
        event.getDispatcher().register(AtCommand.tree());

        // Style 1 - Alternative
        ExampleCommand.register(event.getDispatcher());

        // Style 2
        LiteralArgumentBuilder<ISuggestionProvider> base = ClientCommands.literal("example3");
        HelloCommand.flat(base);
        event.getDispatcher().register(base);

        // Client Override
        event.getDispatcher().register(ClientCommands.literal("time").executes(ClientCommands.command((context) -> {
            context.getSource().sendFeedback(new StringTextComponent("Override Command!"));
            return 1;
        })));

        // Test Command
        TestCommand.register(event.getDispatcher(), event.getType());
    }

}
