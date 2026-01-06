package net.kalbskinder.events.minestom;

import net.kalbskinder.config.ConfigManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.ping.Status;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ServerEvents {
    private final GlobalEventHandler eventHandler;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final ConfigManager config = new ConfigManager();

    public ServerEvents(GlobalEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public void register() {
        final byte[] favicon = loadFaviconBytes();
        int maxPlayers = config.getInt("max-players", 100);
        String version = config.getString("version", "Minestom");
        int protocol = config.getInt("protocol-version", 0);
        String motd = config.getString("motd", "Welcome!");

        eventHandler.addListener(ServerListPingEvent.class, event -> {
            int onlinePlayers = MinecraftServer.getConnectionManager().getOnlinePlayerCount();

            event.setStatus(Status.builder()
                    .favicon(favicon)
                    .description(mm.deserialize(motd))
                    .playerInfo(onlinePlayers, maxPlayers)
                    .versionInfo(new Status.VersionInfo(version, protocol))
                    .build());
        });
    }

    private byte[] loadFaviconBytes() {
        String resourcePath = normalizeLogoResourcePath(config.getString("logo-path", "wrmc.png"));

        try (InputStream dimensionStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (dimensionStream == null) {
                return null;
            }
            BufferedImage image = ImageIO.read(dimensionStream);
            if (image == null) {
                return null;
            }
            if (image.getWidth() != 64 || image.getHeight() != 64) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        try (InputStream rawStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (rawStream == null) return null;
            return rawStream.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }

    private String normalizeLogoResourcePath(String configured) {
        String p = configured != null ? configured.trim() : "";
        if (p.isEmpty()) p = "wrmc.png";
        if (p.startsWith("/")) p = p.substring(1);
        if (!p.contains("/")) p = "images/" + p;
        return p;
    }
}
