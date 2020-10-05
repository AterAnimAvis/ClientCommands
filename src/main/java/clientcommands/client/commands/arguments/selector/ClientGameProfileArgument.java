package clientcommands.client.commands.arguments.selector;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.entity.player.PlayerEntity;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import clientcommands.client.commands.ClientCommandSource;
import clientcommands.client.commands.arguments.impl.ClientEntitySelector;

/**
 * Client Counterpart to {@link GameProfileArgument}
 */
public class ClientGameProfileArgument implements ArgumentType<ClientGameProfileArgument.IProfileProvider> {
    private static final GameProfileArgument IMPL = GameProfileArgument.gameProfile();

    public static Collection<GameProfile> getGameProfiles(CommandContext<ClientCommandSource> context, String name)
        throws CommandSyntaxException {
        return context.getArgument(name, ClientGameProfileArgument.IProfileProvider.class)
                      .getNames(context.getSource());
    }

    public static GameProfileArgument gameProfile() {
        return new GameProfileArgument();
    }

    public ClientGameProfileArgument.IProfileProvider parse(StringReader reader) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '@') {
            EntitySelectorParser parser = new EntitySelectorParser(reader);
            EntitySelector selector = parser.parse();

            if (selector.includesEntities()) throw EntityArgument.ONLY_PLAYERS_ALLOWED.create();

            return new ClientGameProfileArgument.ProfileProvider(selector);
        }

        int initialPosition = reader.getCursor();

        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }

        String s = reader.getString().substring(initialPosition, reader.getCursor());
        return (source) -> {
            ClientPlayNetHandler connection = source.getConnection();
            if (connection == null) throw GameProfileArgument.PLAYER_UNKNOWN.create();

            NetworkPlayerInfo info = connection.getPlayerInfo(s);
            if (info == null) throw GameProfileArgument.PLAYER_UNKNOWN.create();

            return Collections.singleton(info.getGameProfile());
        };
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return IMPL.listSuggestions(context, builder);
    }

    public Collection<String> getExamples() {
        return IMPL.getExamples();
    }

    @FunctionalInterface
    public interface IProfileProvider {
        Collection<GameProfile> getNames(ClientCommandSource source) throws CommandSyntaxException;

    }

    public static class ProfileProvider implements IProfileProvider {
        private final ClientEntitySelector selector;

        public ProfileProvider(EntitySelector selectorIn) {
            this(new ClientEntitySelector(selectorIn));
        }

        public ProfileProvider(ClientEntitySelector selectorIn) {
            this.selector = selectorIn;
        }

        public Collection<GameProfile> getNames(ClientCommandSource source) throws CommandSyntaxException {
            List<PlayerEntity> players = selector.selectPlayers(source);

            if (players.isEmpty()) {
                throw EntityArgument.PLAYER_NOT_FOUND.create();
            }

            List<GameProfile> profiles = Lists.newArrayList();

            for (PlayerEntity player : players) {
                profiles.add(player.getGameProfile());
            }

            return profiles;
        }

    }

}
