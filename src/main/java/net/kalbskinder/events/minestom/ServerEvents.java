package net.kalbskinder.events.minestom;

import net.kalbskinder.config.ConfigManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.ping.Status;

public class ServerEvents {
    private final GlobalEventHandler eventHandler;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final ConfigManager config = new ConfigManager();

    public ServerEvents(GlobalEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public void register() {
        eventHandler.addListener(ServerListPingEvent.class, event -> {
            int onlinePlayers = MinecraftServer.getConnectionManager().getOnlinePlayerCount();
            event.setStatus(Status.builder()
                   .description(mm.deserialize("<green>Welcome to Kalbskinder's Minestom Server!<reset>"))
                           .playerInfo(onlinePlayers, Integer.parseInt(config.get("max-players")))
                            .versionInfo(new Status.VersionInfo(config.get("version"), Integer.parseInt(config.get("protocol-version"))))
                   .build());
        });
    }

}
