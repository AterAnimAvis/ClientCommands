package clientcommands.client.commands.autocomplete;

import net.minecraft.network.play.server.SCommandListPacket;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;

public class LiteralAutocompleteArgumentBuilder<S> extends LiteralArgumentBuilder<S> {

    protected LiteralAutocompleteArgumentBuilder(String literal) {
        super(literal);
    }

    /**
     * Note: This can't be used Server Side as it won't be handled by {@link SCommandListPacket}
     * TODO: Extension PR?
     */
    public static <S> LiteralAutocompleteArgumentBuilder<S> literalAutocomplete(final String name) {
        return new LiteralAutocompleteArgumentBuilder<>(name);
    }

    @Override
    public LiteralAutocompleteCommandNode<S> build() {
        final LiteralAutocompleteCommandNode<S> result = new LiteralAutocompleteCommandNode<>(getLiteral(), getCommand(), getRequirement(), getRedirect(), getRedirectModifier(), isFork());

        for (final CommandNode<S> argument : getArguments()) {
            result.addChild(argument);
        }

        return result;
    }

}
