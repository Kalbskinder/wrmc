package net.kalbskinder.events;

import net.kalbskinder.events.minestom.PlayerEvents;
import net.kalbskinder.events.minestom.ServerEvents;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.instance.InstanceContainer;

public class EventManager {

    public final GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
    public final InstanceContainer instance;

    public EventManager(InstanceContainer instance) {
        this.instance = instance;
    }

    public void registerEvents( ) {
        new PlayerEvents(instance, eventHandler).register();
        new ServerEvents(eventHandler).register();
    }
}
