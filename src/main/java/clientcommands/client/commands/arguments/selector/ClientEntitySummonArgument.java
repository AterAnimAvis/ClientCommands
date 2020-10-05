package clientcommands.client.commands.arguments.selector;

import net.minecraft.command.arguments.EntitySummonArgument;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class ClientEntitySummonArgument {

    public static EntitySummonArgument entitySummon() {
        return new EntitySummonArgument();
    }

    public static <S> ResourceLocation getEntityId(CommandContext<S> context, String name)
        throws CommandSyntaxException {
        return checkIfEntityExists(context.getArgument(name, ResourceLocation.class));
    }

    private static ResourceLocation checkIfEntityExists(ResourceLocation id) throws CommandSyntaxException {
        EntityType<?> type = ForgeRegistries.ENTITIES.getValue(id);

        if (type == null || !type.isSummonable()) throw EntitySummonArgument.ENTITY_UNKNOWN_TYPE.create(id);

        return id;
    }

}
