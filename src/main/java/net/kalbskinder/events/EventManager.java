package net.kalbskinder.events;

import net.kalbskinder.events.chat.ChatEvent;
import net.kalbskinder.events.entity.EntityClickEvent;
import net.kalbskinder.events.entity.EntityDismountEvent;
import net.kalbskinder.events.minestom.PlayerEvents;
import net.kalbskinder.events.minestom.ServerEvents;
import net.kalbskinder.events.npc.NpcClickEvent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.instance.InstanceContainer;

public class EventManager {

    public final GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
    public final InstanceContainer instance;

    public EventManager(InstanceContainer instance) {
        this.instance = instance;
    }

    public void registerEvents() {
        new PlayerEvents(eventHandler, instance).register();
        new ServerEvents(eventHandler).register();
        new NpcClickEvent(eventHandler).register();
        new ChatEvent(eventHandler).register();

        new EntityClickEvent(eventHandler).register();
        new EntityDismountEvent(eventHandler).register();
    }
}
