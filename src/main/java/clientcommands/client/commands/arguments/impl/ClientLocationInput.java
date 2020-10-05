package clientcommands.client.commands.arguments.impl;

import net.minecraft.command.arguments.LocationInput;
import net.minecraft.command.arguments.LocationPart;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import clientcommands.client.commands.ClientCommandSource;
import clientcommands.client.commands.arguments.location.IClientLocation;

/**
 * Client Counterpart to {@link LocationInput}
 */
public class ClientLocationInput implements IClientLocation {

    private final LocationPart x;
    private final LocationPart y;
    private final LocationPart z;

    public ClientLocationInput(LocationPart x, LocationPart y, LocationPart z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3d getPosition(ClientCommandSource source) {
        Vector3d vec3d = source.getPos();
        return new Vector3d(this.x.get(vec3d.x), this.y.get(vec3d.y), this.z.get(vec3d.z));
    }

    public Vector2f getRotation(ClientCommandSource source) {
        Vector2f vec2f = source.getRotation();
        return new Vector2f((float) this.x.get(vec2f.x), (float) this.y.get(vec2f.y));
    }

    public boolean isXRelative() {
        return this.x.isRelative();
    }

    public boolean isYRelative() {
        return this.y.isRelative();
    }

    public boolean isZRelative() {
        return this.z.isRelative();
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof ClientLocationInput)) return false;

        ClientLocationInput other = (ClientLocationInput) obj;
        return x.equals(other.x) && y.equals(other.y) && z.equals(other.z);
    }

    public static ClientLocationInput parseInt(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();

        LocationPart part = LocationPart.parseInt(reader);
        if (!reader.canRead() || reader.peek() != ' ') {
            reader.setCursor(i);
            throw Vec3Argument.POS_INCOMPLETE.createWithContext(reader);
        }
        reader.skip();

        LocationPart part1 = LocationPart.parseInt(reader);
        if (!reader.canRead() || reader.peek() != ' ') {
            reader.setCursor(i);
            throw Vec3Argument.POS_INCOMPLETE.createWithContext(reader);
        }
        reader.skip();

        LocationPart part2 = LocationPart.parseInt(reader);
        return new ClientLocationInput(part, part1, part2);
    }

    public static ClientLocationInput parseDouble(StringReader reader, boolean centerIntegers)
        throws CommandSyntaxException {
        int i = reader.getCursor();

        LocationPart part = LocationPart.parseDouble(reader, centerIntegers);
        if (!reader.canRead() || reader.peek() != ' ') {
            reader.setCursor(i);
            throw Vec3Argument.POS_INCOMPLETE.createWithContext(reader);
        }
        reader.skip();

        LocationPart part1 = LocationPart.parseDouble(reader, centerIntegers);
        if (!reader.canRead() || reader.peek() != ' ') {
            reader.setCursor(i);
            throw Vec3Argument.POS_INCOMPLETE.createWithContext(reader);
        }
        reader.skip();

        LocationPart part2 = LocationPart.parseDouble(reader, centerIntegers);
        return new ClientLocationInput(part, part1, part2);
    }

    /**
     * A location with a delta of 0 for all values (equivalent to <code>~ ~ ~</code> or <code>~0 ~0 ~0</code>)
     */
    public static ClientLocationInput current() {
        return new ClientLocationInput(new LocationPart(true, 0.0D), new LocationPart(true, 0.0D),
                                       new LocationPart(true, 0.0D));
    }

    public int hashCode() {
        int i = this.x.hashCode();
        i = 31 * i + this.y.hashCode();
        i = 31 * i + this.z.hashCode();
        return i;
    }

}
