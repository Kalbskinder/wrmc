package net.kalbskinder.events;

import net.kalbskinder.config.ConfigManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.event.GlobalEventHandler;

public class BasicEvent {
    private final GlobalEventHandler eventHandler;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final ConfigManager config = new ConfigManager();

    public BasicEvent(GlobalEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public GlobalEventHandler getEventHandler() {
        return eventHandler;
    }

    public MiniMessage getMm() {
        return mm;
    }

    public ConfigManager getConfig() {
        return config;
    }
}
