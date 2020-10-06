package clientcommands.client.commands.autocomplete;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class LiteralAutocompleteCommandNode<S> extends LiteralCommandNode<S> {
    private final String literal;

    public LiteralAutocompleteCommandNode(String literal, Command<S> command, Predicate<S> requirement,
                                          CommandNode<S> redirect, RedirectModifier<S> modifier, boolean forks) {
        super(literal, command, requirement, redirect, modifier, forks);

        this.literal = literal;
    }

    @Override
    public CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (literal.toLowerCase().contains(builder.getRemaining().toLowerCase())) {
            return builder.suggest(literal).buildFuture();
        } else {
            return Suggestions.empty();
        }
    }

    @Override
    public LiteralAutocompleteArgumentBuilder<S> createBuilder() {
        final LiteralAutocompleteArgumentBuilder<S>
            builder = LiteralAutocompleteArgumentBuilder.literalAutocomplete(literal);
        builder.requires(getRequirement());
        builder.forward(getRedirect(), getRedirectModifier(), isFork());
        if (getCommand() != null) builder.executes(getCommand());
        return builder;
    }

}
