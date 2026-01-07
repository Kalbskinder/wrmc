package net.kalbskinder.systems.npc;

import java.util.*;

public class NPCManager {
    private static final NPCManager INSTANCE = new NPCManager();

    public static NPCManager getInstance() {
        return INSTANCE;
    }

    private final Map<UUID, NPC> npcList = new HashMap<>();

    private NPCManager() {
    }

    public void addNpc(NPC npc) {
        npcList.put(npc.getUuid(), npc);
    }

    public NPC getNpc(UUID id) {
        return npcList.get(id);
    }

    public Collection<NPC> getAllNpcs() {
        return Collections.unmodifiableCollection(npcList.values());
    }

    public void updateNpc(NPC npc) {
        this.removeNPC(npc.getUuid());
        npcList.put(npc.getUuid(), npc);
    }

    public void removeNPC(UUID id) {
        npcList.remove(id);
    }
}
