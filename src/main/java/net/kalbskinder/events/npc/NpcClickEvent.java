package net.kalbskinder.events.npc;

import net.kalbskinder.events.BasicEvent;
import net.kalbskinder.systems.npc.NpcManager;
import net.minestom.server.entity.EntityType;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerEntityInteractEvent;

public class NpcClickEvent extends BasicEvent {

    private final NpcManager npcManager = NpcManager.getInstance();

    public NpcClickEvent(GlobalEventHandler eventHandler) {
        super(eventHandler);
    }

    public void register() {
        getEventHandler().addListener(PlayerEntityInteractEvent.class, event -> {
            if (event.getTarget().getEntityType() == EntityType.INTERACTION) {
                if (npcManager.isInteractionNpc(npcManager.getNpcUuid(event.getTarget().getUuid()))) {
                    event.getPlayer().sendMessage("You clicked a clickable NPC!");
                } else {
                    event.getPlayer().sendMessage("You clicked a non-clickable NPC!");
                }
            }
        });
    }
}
