package net.kalbskinder.events.npc;

import net.kalbskinder.events.BasicEvent;
import net.kalbskinder.systems.npc.InteractableNpc;
import net.kalbskinder.systems.npc.Npc;
import net.kalbskinder.systems.npc.NpcManager;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class NpcClickEvent extends BasicEvent {

    private final NpcManager npcManager = NpcManager.getInstance();
    private final Logger logger = LoggerFactory.getLogger("NpcClickEvent");

    public NpcClickEvent(GlobalEventHandler eventHandler) {
        super(eventHandler);
    }

    public void register() {
        getEventHandler().addListener(PlayerEntityInteractEvent.class, event -> {
            if (event.getTarget().getEntityType() != EntityType.INTERACTION) return;
            UUID npcUuid = npcManager.getNpcUuid(event.getTarget().getUuid());
            if (npcUuid == null) return;

            Npc npcBase = npcManager.getNpc(npcUuid);
            if (!(npcBase instanceof InteractableNpc npc)) return;

            if (npc.getOnRightClick() == null) {
                if (npc.getRedirectClicks() && npc.getOnLeftClick() != null) {
                    npc.getOnLeftClick().execute(event.getPlayer());
                }
                return;
            }

            npc.getOnRightClick().execute(event.getPlayer());
        });


    }
}
