package clientcommands.client.commands.arguments.selector;

import net.minecraft.command.arguments.TeamArgument;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TranslationTextComponent;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import clientcommands.client.commands.ClientCommandSource;

public class ClientTeamArgument {

    public static final DynamicCommandExceptionType TEAM_NOT_FOUND =
        new DynamicCommandExceptionType((team) -> new TranslationTextComponent("team.notFound", team));

    public static TeamArgument team() {
        return new TeamArgument();
    }

    public static <S> ScorePlayerTeam getTeam(CommandContext<ClientCommandSource> context, String name)
        throws CommandSyntaxException {
        String teamName = context.getArgument(name, String.class);

        Scoreboard scoreboard = context.getSource().getWorld().getScoreboard();
        ScorePlayerTeam team = scoreboard.getTeam(teamName);

        //noinspection ConstantConditions
        if (team == null) throw TEAM_NOT_FOUND.create(teamName);

        return team;
    }

}
