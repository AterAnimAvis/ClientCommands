package clientcommands.client.commands.arguments.selector;

import java.util.List;

import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import clientcommands.client.commands.ClientCommandSource;
import clientcommands.client.commands.arguments.impl.ClientEntitySelector;

import static net.minecraft.command.arguments.EntityArgument.ENTITY_NOT_FOUND;
import static net.minecraft.command.arguments.EntityArgument.PLAYER_NOT_FOUND;

public class ClientEntityArgument {

    public static EntityArgument entity() {
        return EntityArgument.entity();
    }

    public static EntityArgument entities() {
        return EntityArgument.entities();
    }

    public static EntityArgument player() {
        return EntityArgument.player();
    }

    public static EntityArgument players() {
        return EntityArgument.players();
    }

    public static List<? extends Entity> getEntities(CommandContext<ClientCommandSource> context, String name)
        throws CommandSyntaxException {
        List<? extends Entity> entities = getEntitiesAllowingNone(context, name);

        if (entities.isEmpty())
            throw ENTITY_NOT_FOUND.create();

        return entities;
    }

    public static List<PlayerEntity> getPlayers(CommandContext<ClientCommandSource> context, String name)
        throws CommandSyntaxException {
        List<PlayerEntity> players = getPlayersAllowingNone(context, name);

        if (players.isEmpty())
            throw PLAYER_NOT_FOUND.create();

        return players;
    }

    public static Entity getEntity(CommandContext<ClientCommandSource> context, String name)
        throws CommandSyntaxException {
        return new ClientEntitySelector(
            context.getArgument(name, EntitySelector.class)
        ).selectOne(context.getSource());
    }

    public static List<? extends Entity> getEntitiesAllowingNone(CommandContext<ClientCommandSource> context,
                                                                 String name)
        throws CommandSyntaxException {
        return new ClientEntitySelector(
            context.getArgument(name, EntitySelector.class)
        ).select(context.getSource());
    }

    public static PlayerEntity getPlayer(CommandContext<ClientCommandSource> context, String name)
        throws CommandSyntaxException {
        return new ClientEntitySelector(
            context.getArgument(name, EntitySelector.class)
        ).selectOnePlayer(context.getSource());
    }

    public static List<PlayerEntity> getPlayersAllowingNone(CommandContext<ClientCommandSource> context, String name)
        throws CommandSyntaxException {
        return new ClientEntitySelector(
            context.getArgument(name, EntitySelector.class)
        ).selectPlayers(context.getSource());
    }

}
