package clientcommands.client.commands.arguments.selector;

import java.util.Collection;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import clientcommands.client.commands.ClientCommandSource;

public class ClientResourceLocationArgument {

    private static final DynamicCommandExceptionType ADVANCEMENT_NOT_FOUND = new DynamicCommandExceptionType(
        (o) -> new TranslationTextComponent("advancement.advancementNotFound", o));
    private static final DynamicCommandExceptionType RECIPE_NOT_FOUND      = new DynamicCommandExceptionType(
        (o) -> new TranslationTextComponent("recipe.notFound", o));
    private static final DynamicCommandExceptionType PREDICATE_UNKNOWN     = new DynamicCommandExceptionType(
        (o) -> new TranslationTextComponent("predicate.unknown", o));
    private static final DynamicCommandExceptionType ATTRIBUTE_UNKNOWN     = new DynamicCommandExceptionType(
        (o) -> new TranslationTextComponent("attribute.unknown", o));

    public static ResourceLocationArgument resourceLocation() {
        return new ResourceLocationArgument();
    }

    public static <S> ResourceLocation getResourceLocation(CommandContext<S> context, String name) {
        return context.getArgument(name, ResourceLocation.class);
    }

    public static Advancement getAdvancement(CommandContext<ClientCommandSource> context, String name)
        throws CommandSyntaxException {
        ResourceLocation location = getResourceLocation(context, name);

        ClientPlayNetHandler connection = context.getSource().getConnection();
        if (connection == null) throw ADVANCEMENT_NOT_FOUND.create(location);

        Advancement advancement = connection.getAdvancementManager().getAdvancementList().getAdvancement(location);
        if (advancement == null) throw ADVANCEMENT_NOT_FOUND.create(location);

        return advancement;
    }

    public static IRecipe<?> getRecipe(CommandContext<ClientCommandSource> context, String name)
        throws CommandSyntaxException {
        ResourceLocation location = getResourceLocation(context, name);

        ClientPlayNetHandler connection = context.getSource().getConnection();
        if (connection == null) throw RECIPE_NOT_FOUND.create(location);

        return connection.getRecipeManager().getRecipe(location).orElseThrow(() -> RECIPE_NOT_FOUND.create(location));
    }

    //TODO: ILootCondition on Client?
    //public static ILootCondition getLootCondition(CommandContext<ClientCommandSource> context, String name)
    //    throws CommandSyntaxException {
    //    ResourceLocation location = getResourceLocation(context, name);
    //
    //    throw PREDICATE_UNKNOWN.create(location);
    //}

    public static Attribute getAttribute(CommandContext<ClientCommandSource> context, String name)
        throws CommandSyntaxException {
        ResourceLocation location = getResourceLocation(context, name);

        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(location);
        if (attribute == null) throw ATTRIBUTE_UNKNOWN.create(location);

        return attribute;
    }

    public static SuggestionProvider<ISuggestionProvider> SUGGEST_ADVANCEMENTS = (context, builder) -> {
        ClientPlayNetHandler connection = Minecraft.getInstance().getConnection();

        if (connection == null) return Suggestions.empty();

        Collection<Advancement> advancements = connection.getAdvancementManager().getAdvancementList().getAll();

        return ISuggestionProvider.func_212476_a(advancements.stream().map(Advancement::getId), builder);
    };

    public static SuggestionProvider<ISuggestionProvider> SUGGEST_RECIPES = (context, builder) ->
        ISuggestionProvider.func_212476_a((context.getSource()).getRecipeResourceLocations(), builder);

    public static SuggestionProvider<ISuggestionProvider> SUGGEST_ATTRIBUTE = (context, builder) ->
        ISuggestionProvider.suggestIterable(ForgeRegistries.ATTRIBUTES.getKeys(), builder);

}
