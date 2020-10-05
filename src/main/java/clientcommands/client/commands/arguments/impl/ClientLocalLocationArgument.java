package clientcommands.client.commands.arguments.impl;

import java.util.Objects;

import net.minecraft.command.arguments.LocalLocationArgument;
import net.minecraft.command.arguments.LocationPart;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import clientcommands.client.commands.ClientCommandSource;
import clientcommands.client.commands.arguments.location.IClientLocation;

/**
 * Client Counterpart to {@link LocalLocationArgument}
 */
public class ClientLocalLocationArgument implements IClientLocation {

    private final double left;
    private final double up;
    private final double forwards;

    public ClientLocalLocationArgument(double leftIn, double upIn, double forwardsIn) {
        this.left = leftIn;
        this.up = upIn;
        this.forwards = forwardsIn;
    }

    public Vector3d getPosition(ClientCommandSource source) {
        Vector2f vec2f = source.getRotation();
        Vector3d vec3d = source.applyAnchor(source.getEntityAnchorType());

        float f = MathHelper.cos((vec2f.y + 90.0F) * ((float) Math.PI / 180F));
        float f1 = MathHelper.sin((vec2f.y + 90.0F) * ((float) Math.PI / 180F));
        float f2 = MathHelper.cos(-vec2f.x * ((float) Math.PI / 180F));
        float f3 = MathHelper.sin(-vec2f.x * ((float) Math.PI / 180F));
        float f4 = MathHelper.cos((-vec2f.x + 90.0F) * ((float) Math.PI / 180F));
        float f5 = MathHelper.sin((-vec2f.x + 90.0F) * ((float) Math.PI / 180F));
        Vector3d vec3d1 = new Vector3d((f * f2), f3, (f1 * f2));
        Vector3d vec3d2 = new Vector3d((f * f4), f5, (f1 * f4));
        Vector3d vec3d3 = vec3d1.crossProduct(vec3d2).scale(-1.0D);
        double d0 = vec3d1.x * forwards + vec3d2.x * up + vec3d3.x * left;
        double d1 = vec3d1.y * forwards + vec3d2.y * up + vec3d3.y * left;
        double d2 = vec3d1.z * forwards + vec3d2.z * up + vec3d3.z * left;
        return new Vector3d(vec3d.x + d0, vec3d.y + d1, vec3d.z + d2);
    }

    public Vector2f getRotation(ClientCommandSource source) {
        return Vector2f.ZERO;
    }

    public boolean isXRelative() {
        return true;
    }

    public boolean isYRelative() {
        return true;
    }

    public boolean isZRelative() {
        return true;
    }

    public static ClientLocalLocationArgument parse(StringReader reader) throws CommandSyntaxException {
        int initialPosition = reader.getCursor();

        double d0 = parseCoord(reader, initialPosition);
        if (!reader.canRead() || reader.peek() != ' ') {
            reader.setCursor(initialPosition);
            throw Vec3Argument.POS_INCOMPLETE.createWithContext(reader);
        }
        reader.skip();

        double d1 = parseCoord(reader, initialPosition);
        if (!reader.canRead() || reader.peek() != ' ') {
            reader.setCursor(initialPosition);
            throw Vec3Argument.POS_INCOMPLETE.createWithContext(reader);
        }
        reader.skip();

        double d2 = parseCoord(reader, initialPosition);
        return new ClientLocalLocationArgument(d0, d1, d2);
    }

    private static double parseCoord(StringReader reader, int start) throws CommandSyntaxException {
        if (!reader.canRead()) throw LocationPart.EXPECTED_DOUBLE.createWithContext(reader);

        if (reader.peek() != '^') {
            reader.setCursor(start);
            throw Vec3Argument.POS_MIXED_TYPES.createWithContext(reader);
        }

        reader.skip();
        return reader.canRead() && reader.peek() != ' ' ? reader.readDouble() : 0.0D;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof ClientLocalLocationArgument)) return false;

        ClientLocalLocationArgument other = (ClientLocalLocationArgument) obj;
        return this.left == other.left && this.up == other.up && this.forwards == other.forwards;
    }

    public int hashCode() {
        return Objects.hash(this.left, this.up, this.forwards);
    }

}
