package net.kalbskinder.systems.npc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class NpcManager {
    private static final NpcManager INSTANCE = new NpcManager();
    private final Logger logger = LoggerFactory.getLogger("NpcManager");

    public static NpcManager getInstance() {
        return INSTANCE;
    }

    private final Map<UUID, Npc> npcList = new HashMap<>();

    public void addNpc(Npc npc) {
        npcList.put(npc.getUuid(), npc);
    }

    public Npc getNpc(UUID id) {
        return npcList.get(id);
    }

    public Collection<Npc> getAllNpcs() {
        return Collections.unmodifiableCollection(npcList.values());
    }

    public void updateNpc(Npc npc) {
        this.removeNPC(npc.getUuid());
        npcList.put(npc.getUuid(), npc);
    }

    public void removeNPC(UUID id) {
        npcList.remove(id);
    }

    public boolean isInteractionNpc(UUID id) {
        for (Map.Entry<UUID, Npc> entry : npcList.entrySet()) {
            if (entry.getValue() instanceof InteractableNpc &&
                    entry.getKey().equals(id)) {
                logger.debug("NPC with UUID {} is an InteractableNpc.", id);
                return true;
            }
        }
        return false;
    }

    public UUID getNpcUuid(UUID interactionUuid) {
        for (Map.Entry<UUID, Npc> entry : npcList.entrySet()) {
            if (entry.getValue() instanceof InteractableNpc) {
                InteractableNpc interactableNpc = (InteractableNpc) entry.getValue();
                if (interactableNpc.getInteractionEntityUuid().equals(interactionUuid)) {
                    logger.debug("Found NPC UUID {} with skinUuuid {} for interaction entity UUID {}.",
                            entry.getKey(), interactableNpc.getSkinUuid(), interactionUuid);
                    return entry.getKey();
                }
            }
        }
        return null;
    }
}
