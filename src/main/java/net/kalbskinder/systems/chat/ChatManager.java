package net.kalbskinder.systems.chat;


import java.util.*;

public class ChatManager {
    /** TextDisplayEntityUuid, ChatMessage */
    private final Map<UUID, String> messages = new HashMap<>();
    /** PlayerUuid, (TextDisplayEntityUuid, ChatMessage) */
    private final Map<UUID, Map<UUID, String>> playerMessages = new HashMap<>();

    public Map<UUID, String> getMessages(UUID uuid) {
        return playerMessages.get(uuid);
    }

    public void addMessage(UUID uuid, String message) {
        UUID messageUuid = UUID.randomUUID();
        messages.put(messageUuid, message);
        if (playerMessages.containsKey(uuid)) {
            playerMessages.get(uuid).put(messageUuid, message);
        } else {
            Map<UUID, String> newMessageMap = new HashMap<>();
            newMessageMap.put(messageUuid, message);
            playerMessages.put(uuid, newMessageMap);
        }
    }
}
