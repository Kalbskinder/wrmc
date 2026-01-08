package net.kalbskinder.events.chat;

import net.kalbskinder.events.BasicEvent;
import net.kalbskinder.systems.chat.ChatSystem;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChatEvent;

public class ChatEvent extends BasicEvent {
    private final ChatSystem chatSystem = ChatSystem.getInstance();

    public ChatEvent(GlobalEventHandler eventHandler) {
        super(eventHandler);
    }

    public void register() {
        getEventHandler().addListener(PlayerChatEvent.class, event -> {
            // Keep chat local to the bubble display.
            event.setCancelled(true);

            String message = event.getRawMessage();
            if (message == null) return;

            chatSystem.onPlayerChat(event.getPlayer(), message);
        });
    }
}
