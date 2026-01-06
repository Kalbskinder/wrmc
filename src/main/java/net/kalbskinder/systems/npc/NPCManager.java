package net.kalbskinder.systems.npc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NPCManager {
    private final Map<UUID, NPC> npcList = new HashMap<>();

    public void addNpc(NPC npc) {
        npcList.put(npc.getUuid(), npc);
    }

    public NPC getNpc(UUID id) {
        return npcList.get(id);
    }

    public void updateNpc(UUID id, NPC npc) {
        this.removeNPC(id);
        npcList.put(id, npc);
    }

    public void removeNPC(UUID id) {
        npcList.remove(id);
    }
}
