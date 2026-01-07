package net.kalbskinder.events.minestom;

import net.kalbskinder.events.BasicEvent;
import net.kalbskinder.events.InstanceEvent;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSkinInitEvent;
import net.minestom.server.instance.InstanceContainer;

public class PlayerEvents extends InstanceEvent {

    public PlayerEvents(GlobalEventHandler eventHandler, InstanceContainer instance) {
        super(eventHandler, instance);
    }

    public void register() {
        getEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(getInstance());
            player.setRespawnPoint(new Pos(0, 42, 0));
        });

        getEventHandler().addListener(PlayerSkinInitEvent.class, event -> {
            PlayerSkin skin = PlayerSkin.fromUsername("Kalbskinder");
            event.setSkin(skin);
        });
    }
}
