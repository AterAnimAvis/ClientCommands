package clientcommands.client.example;

import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import clientcommands.client.commands.ClientCommandSource;
import clientcommands.client.commands.arguments.generic.ClientRangeArgument;
import clientcommands.client.commands.arguments.generic.ClientTimeArgument;
import clientcommands.client.commands.arguments.generic.ClientUUIDArgument;
import clientcommands.client.commands.arguments.location.ClientBlockPosArgument;
import clientcommands.client.commands.arguments.location.ClientColumnPosArgument;
import clientcommands.client.commands.arguments.location.ClientVec2Argument;
import clientcommands.client.commands.arguments.location.ClientVec3Argument;
import clientcommands.client.commands.arguments.selector.ClientBlockPredicateArgument;
import clientcommands.client.commands.arguments.selector.ClientBlockStateArgument;
import clientcommands.client.commands.arguments.selector.ClientDimensionArgument;
import clientcommands.client.commands.arguments.selector.ClientEntityArgument;
import clientcommands.client.commands.arguments.selector.ClientEntitySummonArgument;
import clientcommands.client.commands.arguments.selector.ClientGameProfileArgument;
import clientcommands.client.commands.arguments.selector.ClientPotionArgument;
import clientcommands.client.commands.arguments.selector.ClientResourceLocationArgument;
import clientcommands.client.commands.arguments.selector.ClientSwizzleArgument;
import clientcommands.client.commands.arguments.selector.ClientTeamArgument;
import clientcommands.client.commands.arguments.text.ClientColorArgument;
import clientcommands.client.commands.arguments.text.ClientComponentArgument;
import clientcommands.client.event.RegisterClientCommandsEvent;

import static clientcommands.client.commands.ClientCommands.argument;
import static clientcommands.client.commands.ClientCommands.command;
import static clientcommands.client.commands.ClientCommands.literal;

public class TestCommand {

    public static int COUNT               = 0;
    public static int COUNT_UNIMPLEMENTED = 0;
    public static int COUNT_EXTRA         = 0;

    private static final Supplier<ArgumentType<String>> UNIMPLEMENTED        = StringArgumentType::word;
    private static final ArgumentGetter<String>         UNIMPLEMENTED_GETTER = StringArgumentType::getString;

    private static final SuggestionProvider<ISuggestionProvider> UNIMPLEMENTED_PROVIDER = (context, builder) ->
        ISuggestionProvider.suggest(new String[] {"unimplemented"}, builder);

    /**
     * @see ArgumentTypes
     */
    public static void register(CommandDispatcher<ISuggestionProvider> dispatcher,
                                RegisterClientCommandsEvent.Type type) {
        /* Branch */
        LiteralArgumentBuilder<ISuggestionProvider> root = literal("client-command-test");

        /* Arguments */
        COUNT = 0;
        COUNT_UNIMPLEMENTED = 0;
        COUNT_EXTRA = 0;

        //@formatter:off

        // Brigadier (6)
        register(root, "brigadier:bool", BoolArgumentType::bool, BoolArgumentType::getBool, true);
        register(root, "brigadier:float", FloatArgumentType::floatArg, FloatArgumentType::getFloat, true);
        register(root, "brigadier:double", DoubleArgumentType::doubleArg, DoubleArgumentType::getDouble, true);
        register(root, "brigadier:integer", IntegerArgumentType::integer, IntegerArgumentType::getInteger, true);
        register(root, "brigadier:long", LongArgumentType::longArg, LongArgumentType::getLong, true);
        register(root, "brigadier:string", StringArgumentType::string, StringArgumentType::getString, true);
        register(root, "brigadier:word", StringArgumentType::word, StringArgumentType::getString);
        register(root, "brigadier:greedy", StringArgumentType::greedyString, StringArgumentType::getString);

        // Minecraft (38)
        register(root, "minecraft:entity", ClientEntityArgument::entity, ClientEntityArgument::getEntity, true);
        register(root, "minecraft:entities", ClientEntityArgument::entities, ClientEntityArgument::getEntities);
        register(root, "minecraft:entities?", ClientEntityArgument::entities, ClientEntityArgument::getEntitiesAllowingNone);
        register(root, "minecraft:player", ClientEntityArgument::player, ClientEntityArgument::getPlayer);
        register(root, "minecraft:players", ClientEntityArgument::players, ClientEntityArgument::getPlayers);
        register(root, "minecraft:players?", ClientEntityArgument::players, ClientEntityArgument::getPlayersAllowingNone);
        register(root, "minecraft:game_profile", ClientGameProfileArgument::gameProfile, ClientGameProfileArgument::getGameProfiles, true);
        register(root, "minecraft:block_pos", ClientBlockPosArgument::blockPos, ClientBlockPosArgument::getBlockPos, true);
        register(root, "minecraft:loaded_block_pos", ClientBlockPosArgument::blockPos, ClientBlockPosArgument::getLoadedBlockPos);
        register(root, "minecraft:column_pos", ClientColumnPosArgument::columnPos, ClientColumnPosArgument::fromBlockPos, true);
        register(root, "minecraft:vec3", ClientVec3Argument::vec3, ClientVec3Argument::getVec3, true);
        register(root, "minecraft:vec3_center", () -> ClientVec3Argument.vec3(true), ClientVec3Argument::getVec3);
        register(root, "minecraft:vec2", ClientVec2Argument::vec2, ClientVec2Argument::getVec2f, true);
        // -------------------------------------------------------------------------------------------------------------
        // The Following are mostly untested.
        register(root, "minecraft:block_state", ClientBlockStateArgument::blockState, ClientBlockStateArgument::getBlockState, true);
        register(root, "minecraft:block_predicate", ClientBlockPredicateArgument::blockPredicate, ClientBlockPredicateArgument::getBlockPredicate, true);
        register(root, "minecraft:item_stack", UNIMPLEMENTED, UNIMPLEMENTED_GETTER, true); // ClientItemArgument.class, new ArgumentSerializer<>(ItemArgument::item));
        register(root, "minecraft:item_predicate", UNIMPLEMENTED, UNIMPLEMENTED_GETTER, true); // ItemPredicateArgument.class, new ArgumentSerializer<>(ItemPredicateArgument::itemPredicate));
        register(root, "minecraft:color", ClientColorArgument::color, ClientColorArgument::getColor, true);
        register(root, "minecraft:component", ClientComponentArgument::component, ClientComponentArgument::getComponent, true);
        register(root, "minecraft:message", UNIMPLEMENTED, UNIMPLEMENTED_GETTER, true); // MessageArgument.class, new ArgumentSerializer<>(MessageArgument::message));
        register(root, "minecraft:nbt_compound_tag", UNIMPLEMENTED, UNIMPLEMENTED_GETTER, true); // NBTCompoundTagArgument.class, new ArgumentSerializer<>(NBTCompoundTagArgument::nbt));
        register(root, "minecraft:nbt_tag", UNIMPLEMENTED, UNIMPLEMENTED_GETTER, true); // NBTTagArgument.class, new ArgumentSerializer<>(NBTTagArgument::func_218085_a));
        register(root, "minecraft:nbt_path", UNIMPLEMENTED, UNIMPLEMENTED_GETTER, true); // NBTPathArgument.class, new ArgumentSerializer<>(NBTPathArgument::nbtPath));
        register(root, "minecraft:objective", UNIMPLEMENTED, UNIMPLEMENTED_GETTER, true); // ObjectiveArgument.class, new ArgumentSerializer<>(ObjectiveArgument::objective));
        register(root, "minecraft:objective_criteria", UNIMPLEMENTED, UNIMPLEMENTED_GETTER, true); // ObjectiveCriteriaArgument.class, new ArgumentSerializer<>(ObjectiveCriteriaArgument::objectiveCriteria));
        register(root, "minecraft:operation", UNIMPLEMENTED, UNIMPLEMENTED_GETTER, true); // OperationArgument.class, new ArgumentSerializer<>(OperationArgument::operation));
        register(root, "minecraft:particle", UNIMPLEMENTED, UNIMPLEMENTED_GETTER, true); // ParticleArgument.class, new ArgumentSerializer<>(ParticleArgument::particle));
        register(root, "minecraft:angle", UNIMPLEMENTED, UNIMPLEMENTED_GETTER, true); // AngleArgument.class, new ArgumentSerializer<>(AngleArgument::func_242991_a));
        register(root, "minecraft:rotation", UNIMPLEMENTED, UNIMPLEMENTED_GETTER, true); // RotationArgument.class, new ArgumentSerializer<>(RotationArgument::rotation));
        register(root, "minecraft:scoreboard_slot", UNIMPLEMENTED, UNIMPLEMENTED_GETTER, true); // ScoreboardSlotArgument.class, new ArgumentSerializer<>(ScoreboardSlotArgument::scoreboardSlot));
        register(root, "minecraft:score_holder", UNIMPLEMENTED, UNIMPLEMENTED_GETTER, true); // ScoreHolderArgument.class, new ScoreHolderArgument.Serializer());
        register(root, "minecraft:swizzle", ClientSwizzleArgument::swizzle, ClientSwizzleArgument::getSwizzle, true);
        register(root, "minecraft:team", ClientTeamArgument::team, ClientTeamArgument::getTeam, true);
        register(root, "minecraft:item_slot", UNIMPLEMENTED, UNIMPLEMENTED_GETTER, true); // SlotArgument.class, new ArgumentSerializer<>(SlotArgument::slot));
        register(root, "minecraft:resource_location", ClientResourceLocationArgument::resourceLocation, ClientResourceLocationArgument::getResourceLocation, true);
        register(root, "minecraft:recipe", ClientResourceLocationArgument::resourceLocation, ClientResourceLocationArgument.SUGGEST_RECIPES, ClientResourceLocationArgument::getRecipe);
        register(root, "minecraft:advancement", ClientResourceLocationArgument::resourceLocation, ClientResourceLocationArgument.SUGGEST_ADVANCEMENTS, ClientResourceLocationArgument::getAdvancement);
        register(root, "minecraft:attribute", ClientResourceLocationArgument::resourceLocation, ClientResourceLocationArgument.SUGGEST_ATTRIBUTE, ClientResourceLocationArgument::getAttribute);
        register(root, "minecraft:mob_effect", ClientPotionArgument::mobEffect, ClientPotionArgument::getMobEffect, true);
        register(root, "minecraft:function", UNIMPLEMENTED, UNIMPLEMENTED_GETTER, true); // FunctionArgument.class, new ArgumentSerializer<>(FunctionArgument::function));
        register(root, "minecraft:entity_anchor", UNIMPLEMENTED, UNIMPLEMENTED_GETTER, true); // EntityAnchorArgument.class, new ArgumentSerializer<>(EntityAnchorArgument::entityAnchor));
        register(root, "minecraft:int_range", ClientRangeArgument::intRange, ClientRangeArgument::getIntRange, true);
        register(root, "minecraft:float_range", ClientRangeArgument::floatRange, ClientRangeArgument::getFloatBound, true);
        register(root, "minecraft:item_enchantment", UNIMPLEMENTED, UNIMPLEMENTED_GETTER, true); // EnchantmentArgument.class, new ArgumentSerializer<>(EnchantmentArgument::enchantment));
        register(root, "minecraft:entity_summon", ClientEntitySummonArgument::entitySummon, ClientEntitySummonArgument::getEntityId, true);
        register(root, "minecraft:dimension", ClientDimensionArgument::dimension, ClientDimensionArgument::getDimensionArgument, true);
        register(root, "minecraft:dimension_world", ClientDimensionArgument::dimension, ClientDimensionArgument::getDimensionKey);
        register(root, "minecraft:time", ClientTimeArgument::timeArgument, ClientTimeArgument::getTime, true);
        register(root, "minecraft:uuid", ClientUUIDArgument::uuid, ClientUUIDArgument::getUUID, true);

        if (type == RegisterClientCommandsEvent.Type.EXECUTABLE) {
            Minecraft.getInstance().player.sendMessage(
                new StringTextComponent("Arguments " + (COUNT - COUNT_UNIMPLEMENTED) + " / " + COUNT + " (expected: 38 + 6) + extra " + COUNT_EXTRA),
                                                       Util.field_240973_b_);
        }

        //@formatter:on

        /* Register */
        dispatcher.register(root);
    }

    public static <T> void register(
        LiteralArgumentBuilder<ISuggestionProvider> root,
        String name,
        Supplier<ArgumentType<T>> creator,
        ArgumentGetter<?> getter
    ) {
        register(root, name, creator, getter, false);
    }

    public static <T> void register(
        LiteralArgumentBuilder<ISuggestionProvider> root,
        String name,
        Supplier<ArgumentType<T>> creator,
        SuggestionProvider<ISuggestionProvider> provider,
        ArgumentGetter<?> getter
    ) {
        register(root, name, creator, getter, false);
    }

    public static <T> void register(
        LiteralArgumentBuilder<ISuggestionProvider> root,
        String name,
        Supplier<ArgumentType<T>> creator,
        ArgumentGetter<?> getter,
        boolean count
    ) {
        register(root, name, creator, null, getter, count);
    }

    public static <T> void register(
        LiteralArgumentBuilder<ISuggestionProvider> root,
        String name,
        Supplier<ArgumentType<T>> creator,
        @Nullable SuggestionProvider<ISuggestionProvider> provider,
        ArgumentGetter<?> getter,
        boolean count
    ) {
        if (count) COUNT++;
        else COUNT_EXTRA++;

        RequiredArgumentBuilder<ISuggestionProvider, T> arg = argument("arg", creator.get());

        if (Objects.equals(creator, UNIMPLEMENTED)) {
            arg.suggests(UNIMPLEMENTED_PROVIDER);
            if (count) COUNT_UNIMPLEMENTED++;
        } else {
            if (provider != null) arg.suggests(provider);
        }

        root.then(literal(name).then(arg.executes(command(echo(name, getter)))));
    }

    public static <T> Command<ClientCommandSource> echo(String name, ArgumentGetter<T> getter) {
        return (context) -> {
            context.getSource()
                   .sendFeedback(new StringTextComponent(name + " : " + getter.apply(context, "arg").toString()));

            return 1;
        };
    }

    public interface ArgumentGetter<T> {

        T apply(CommandContext<ClientCommandSource> context, String name) throws CommandSyntaxException;

    }

}
