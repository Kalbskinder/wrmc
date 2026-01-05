package net.kalbskinder.events.minestom;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSkinInitEvent;
import net.minestom.server.instance.InstanceContainer;

public class PlayerEvents {
    private final InstanceContainer instance;
    private final GlobalEventHandler eventHandler;

    public PlayerEvents(InstanceContainer instance, GlobalEventHandler eventHandler) {
        this.instance = instance;
        this.eventHandler = eventHandler;
    }

    public void register() {
        eventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instance);
            player.setRespawnPoint(new Pos(0, 42, 0));
        });

        eventHandler.addListener(PlayerSkinInitEvent.class, event -> {
            PlayerSkin skin = PlayerSkin.fromUsername("Kalbskinder");
            event.setSkin(skin);
        });
    }
}
