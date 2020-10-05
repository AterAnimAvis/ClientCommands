package clientcommands.client.event;

/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

import net.minecraft.command.ISuggestionProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.ListenerList;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventListenerHelper;

import com.mojang.brigadier.CommandDispatcher;

/**
 * ClientCommands are rebuilt whenever you change Dimensions or when the servers reloads datapacks. You can use this
 * event to register your commands.
 * <p>
 * Warning: Registering a Command may override a Server Command
 * <p>
 * The event is fired on the {@link MinecraftForge#EVENT_BUS}
 */
public class RegisterClientCommandsEvent extends Event {

    private CommandDispatcher<ISuggestionProvider> dispatcher; // TODO: Make Final
    private Type                                   type;       // TODO: Make Final

    public RegisterClientCommandsEvent(CommandDispatcher<ISuggestionProvider> dispatcher, Type type) {
        this.dispatcher = dispatcher;
        this.type = type;
    }

    public CommandDispatcher<ISuggestionProvider> getDispatcher() {
        return dispatcher;
    }

    public Type getType() {
        return type;
    }

    public static enum Type {
        EXECUTABLE,
        SUGGESTIONS
    }

    /* ================================================================================================= HotSwap ==== */

    private static final ListenerList LISTENER_LIST;

    static {
        LISTENER_LIST = new ListenerList(EventListenerHelper.getListenerList(RegisterClientCommandsEvent.class.getSuperclass()));
    }

    @Override
    public ListenerList getListenerList() {
        return LISTENER_LIST;
    }

    /**
     * Added to allow HotSwap reloading of Classes.
     */
    public RegisterClientCommandsEvent() {
        super();
    }

}
