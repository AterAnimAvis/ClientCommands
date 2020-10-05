package clientcommands.client.commands.arguments.location;

import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

import clientcommands.client.commands.ClientCommandSource;

/**
 * Client Counterpart of {@link ILocationArgument}
 */
public interface IClientLocation {

    Vector3d getPosition(ClientCommandSource source);

    Vector2f getRotation(ClientCommandSource source);

    default BlockPos getBlockPos(ClientCommandSource source) {
        return new BlockPos(this.getPosition(source));
    }

    boolean isXRelative();

    boolean isYRelative();

    boolean isZRelative();

}
