package clientcommands.client.commands.impl;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.command.CommandException;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SCommandListPacket;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.google.common.base.Throwables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import clientcommands.R;
import clientcommands.client.commands.ClientCommandSource;
import clientcommands.client.event.ClientCommandEvent;
import clientcommands.client.event.RegisterClientCommandsEvent;
import clientcommands.common.Markers;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Mod.EventBusSubscriber(modid = R.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientCommandsImpl {

    public static final Logger LOGGER = LogManager.getLogger();

    public static CommandDispatcher<ISuggestionProvider> dispatcher = new CommandDispatcher<>();

    private static void setup() {
        dispatcher = new CommandDispatcher<>();
        register(dispatcher, RegisterClientCommandsEvent.Type.EXECUTABLE);
    }

    /* ============================================================================================ Registration ==== */

    static void register(final String source) {
        Minecraft minecraft = Minecraft.getInstance();

        /* Login: ? - TODO: Test if we can get away without enqueue */
        /* Packets: We need to delay until the packet-handler has been called on the main thread */
        minecraft.enqueue(() -> {
            LOGGER.info(Markers.COMMAND, "Registering Client Commands from {}", source);

            CommandDispatcher<ISuggestionProvider> dispatcher =
                Objects.requireNonNull(minecraft.getConnection()).getCommandDispatcher();

            /* Executable */
            ClientCommandsImpl.setup();

            /* Suggestions */
            ClientCommandsImpl.register(dispatcher, RegisterClientCommandsEvent.Type.SUGGESTIONS);
        });
    }

    static void register(CommandDispatcher<ISuggestionProvider> dispatcher, RegisterClientCommandsEvent.Type type) {
        MinecraftForge.EVENT_BUS.post(new RegisterClientCommandsEvent(dispatcher, type));
    }

    /* ================================================================================================= Handler ==== */

    @SubscribeEvent
    public static void registerClientCommands(ClientPlayerNetworkEvent.LoggedInEvent event) {
        NetworkManager manager = event.getNetworkManager();
        assert manager != null;

        /* Register our Commands */
        register("Login");

        /* Register a Packet Listener so we can handle CommandDispatcher getting reset when travelling dimensions */
        /* TODO: Replace with a Hook in Forge PR */
        LOGGER.info(Markers.COMMAND, "Registering Channel Handler");
        manager.channel()
               .pipeline()
               .addBefore("packet_handler", R.ID + "_handler", new PacketHandler());
    }

    @SubscribeEvent
    public static void handleCommands(ClientChatEvent event) {
        final String message = event.getMessage();

        /* If not a command message of at least length 2 return */
        if (!message.startsWith("/") && message.length() > 1) return;

        /* Create our Source */
        final Minecraft minecraft = Minecraft.getInstance();
        final ClientPlayerEntity player = minecraft.player;

        if (player == null) return;

        ClientCommandSource source = new ClientCommandSource(player);

        /* Try parse command */
        StringReader input = new StringReader(message);

        /* Remove Initial / */
        if (input.peek() == '/') input.skip();

        try {
            ParseResults<ISuggestionProvider> results = emitEvent(dispatcher.parse(input, source)).orElse(null);

            /* Event Cancelled */
            if (results == null) return;

            /* Input has only matched "/" so skip, cursor will only be bigger than one when it matches a whole word */
            if (results.getReader().getCursor() < 2) return;

            /* Exceptions whilst parsing */
            if (results.getExceptions().size() > 0) return; // This won't occur ? due to throwing in emitEvent

            /* Don't send this to the server */
            event.setCanceled(true);

            try {
                /* Execute our dispatcher */
                dispatcher.execute(results);
            } catch (CommandSyntaxException e) {
                /* If we get an unknown command / unknown argument work out if we should forward to the server */
                if (e.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand() ||
                    e.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument()) {

                    CommandDispatcher<ISuggestionProvider> suggestionDispatcher = player.connection
                        .getCommandDispatcher();

                    /* Try parse command */
                    StringReader actual = new StringReader(results.getReader().getString());
                    if (actual.peek() == '/') actual.skip();
                    try {
                        suggestionDispatcher.execute(actual, source);

                        /* Suggestion Parse Successfully probably a Server Command */
                        event.setCanceled(false);
                        return;
                    } catch (CommandSyntaxException other) {
                        /* Parsed command for longer. probably a Server Command */
                        if (e.getCursor() < other.getCursor()) { //TODO: <= ?
                            event.setCanceled(false);
                            return;
                        }
                    } catch (Exception ignored) { /* NO-OP */ }
                }

                /* Not an unknown command / argument & unlikely to be a server command pass on to our error handling */
                throw e;
            }
        } catch (CommandException e) {
            /* Command generated an expected Exception */
            source.sendErrorMessage(e.getComponent());
        } catch (CommandSyntaxException e) {
            /* Command Syntax */
            source.sendErrorMessage(TextComponentUtils.toTextComponent(e.getRawMessage()));
            ITextComponent error = generateSuggestion(message, e);
            if (error != null) source.sendErrorMessage(error);
        } catch (Exception e) {
            /* Command generated an unexpected Exception */
            source.sendErrorMessage(generateException(message, e));

            if (SharedConstants.developmentMode) {
                source.sendErrorMessage(new StringTextComponent(Util.getMessage(e)));
                LOGGER.error("'" + message + "' threw an exception", e);
            }
        }

        /* If we get this far add the message to our chat history */
        addToHistory(message);
    }

    /**
     * @see net.minecraft.command.Commands#handleCommand
     */
    @Nullable
    static ITextComponent generateSuggestion(String message, CommandSyntaxException e) {
        if (e.getInput() != null && e.getCursor() >= 0) {
            int pos = Math.min(e.getInput().length(), e.getCursor());

            IFormattableTextComponent error = new StringTextComponent("")
                .func_240699_a_/* applyTextStyle */(TextFormatting.GRAY)
                .func_240700_a_/* applyTextStyle */(style -> style.func_240715_a_/* setClickEvent */(
                    new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, message))
                );

            if (pos > 10) error.func_240702_b_/* appendText */("...");

            error.func_240702_b_/* appendText */(e.getInput().substring(Math.max(0, pos - 10), pos));

            if (pos < e.getInput().length()) {
                String end = e.getInput().substring(pos);
                ITextComponent place = new StringTextComponent(end)
                    .func_240701_a_/* applyTextStyles */(TextFormatting.RED, TextFormatting.UNDERLINE);

                error.func_230529_a_/* appendSibling */(place);
            }
            error.func_230529_a_/* appendSibling */(new TranslationTextComponent("command.context.here")
                                    .func_240701_a_/* applyTextStyles */(TextFormatting.RED, TextFormatting.ITALIC));
            return error;
        }

        return null;
    }

    static ITextComponent generateException(String command, Exception exception) {
        String message = exception.getMessage() == null ? exception.getClass().getName() : exception.getMessage();
        IFormattableTextComponent component = new StringTextComponent(message);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.error("Command exception: {}", command, exception);
            StackTraceElement[] stackTrace = exception.getStackTrace();

            for (int j = 0; j < Math.min(stackTrace.length, 3); ++j) {
                String fileName = stackTrace[j].getFileName();
                if (fileName == null) fileName = "<unknown file>";

                component.func_240702_b_/* appendText */("\n\n")
                         .func_240702_b_(stackTrace[j].getMethodName())
                         .func_240702_b_("\n ")
                         .func_240702_b_(fileName)
                         .func_240702_b_(":")
                         .func_240702_b_(String.valueOf(stackTrace[j].getLineNumber()));
            }
        }

        return new TranslationTextComponent("command.failed").func_240700_a_/* applyTextStyle */(
            (style) -> style.func_240716_a_/* setHoverText */(new HoverEvent(HoverEvent.Action.field_230550_a_/* SHOW_TEXT */, component)));
    }

    static Optional<ParseResults<ISuggestionProvider>> emitEvent(ParseResults<ISuggestionProvider> results) {
        ClientCommandEvent commandEvent = new ClientCommandEvent(results);
        if (MinecraftForge.EVENT_BUS.post(commandEvent)) return Optional.empty();
        if (commandEvent.getException() != null) Throwables.throwIfUnchecked(commandEvent.getException());
        return Optional.of(commandEvent.getParseResults());
    }

    static void addToHistory(String message) {
        Minecraft.getInstance().ingameGUI.getChatGUI().addToSentMessages(message);
    }

    /**
     * TODO: Replace with a Hook in Forge PR
     *
     * @see ClientPlayNetHandler#handleCommandList(SCommandListPacket)
     */
    static class PacketHandler extends SimpleChannelInboundHandler<IPacket<?>> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, IPacket<?> msg) {
            /* Pass packet through to packet_handler */
            ctx.fireChannelRead(msg);

            /* If we just got a SCommandListPacket we need to re-add our commands */
            if (msg instanceof SCommandListPacket) register("Packet");
        }

    }

}
