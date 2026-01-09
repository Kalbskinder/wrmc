package net.kalbskinder.events.entity;

import net.kalbskinder.events.BasicEvent;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerEntityInteractEvent;

public class EntityClickEvent extends BasicEvent {

    public EntityClickEvent(GlobalEventHandler eventHandler) {
        super(eventHandler);
    }

    public void register() {
        getEventHandler().addListener(PlayerEntityInteractEvent.class, event -> {
            Player player = event.getPlayer();
            Entity entity = event.getTarget();

            if (entity.getEntityType() == EntityType.ARMOR_STAND) {
                entity.addPassenger(player);
            }
        });
    }
}
