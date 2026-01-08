package net.kalbskinder.events.chat;

import net.kalbskinder.events.BasicEvent;
import net.kalbskinder.systems.chat.ChatSystem;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.sound.SoundEvent;

public class ChatEvent extends BasicEvent {
    private final ChatSystem chatSystem = ChatSystem.getInstance();

    public ChatEvent(GlobalEventHandler eventHandler) {
        super(eventHandler);
    }

    public void register() {
        getEventHandler().addListener(PlayerChatEvent.class, this::accept);
    }

    private void accept(PlayerChatEvent event) {
        // Keep chat local to the bubble display.
        event.setCancelled(true);

        String message = event.getRawMessage();
        if (message == null) return;

        Player player = event.getPlayer();
        chatSystem.onPlayerChat(player, message);
        player.playSound(
                Sound.sound(
                        SoundEvent.ENTITY_CHICKEN_EGG,
                        Sound.Source.PLAYER,
                        1.0f,
                        1.3f
                ),
                player.getPosition()
        );
    }
}
