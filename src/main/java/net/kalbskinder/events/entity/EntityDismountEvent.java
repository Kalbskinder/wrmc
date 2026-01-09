package net.kalbskinder.events.entity;

import net.kalbskinder.events.BasicEvent;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerStartSneakingEvent;

public class EntityDismountEvent extends BasicEvent {
    public EntityDismountEvent(GlobalEventHandler eventHandler) {
        super(eventHandler);
    }

    public void register() {
        getEventHandler().addListener(PlayerStartSneakingEvent.class, event -> {
            Player player = event.getPlayer();
            if (player.getVehicle() != null) {
                player.getVehicle().removePassenger(player);
                player.teleport(player.getPosition().add(1.2, 1, 0)); // Remove player to the left to avoid being pushed into the ground
            }
        });
    }
}
