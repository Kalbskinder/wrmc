package net.kalbskinder.systems.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shows recent chat messages above each player's head.
 *
 * Contract:
 * - Keeps the 3 most recent messages per player.
 * - Messages expire after 4 seconds.
 * - Newest message is displayed on the bottom line.
 * - The hologram moves with the player.
 */
public final class ChatSystem {

    private static final ChatSystem INSTANCE = new ChatSystem();

    public static ChatSystem getInstance() {
        return INSTANCE;
    }

    private static final int MAX_LINES = 1;
    private static final Duration MESSAGE_TTL = Duration.ofSeconds(6);

    // Tunables: where to place the bubble.
    private static final double BASE_Y_OFFSET = 2.35; // above head
    private static final String PAD_X = "  "; // horizontal padding

    private final Map<UUID, PlayerState> states = new ConcurrentHashMap<>();

    private ChatSystem() {
    }

    public void onPlayerChat(Player player, String message) {
        if (player == null || player.isRemoved()) return;
        if (message == null) return;

        String trimmed = message.trim();
        if (trimmed.isEmpty()) return;

        PlayerState state = states.computeIfAbsent(player.getUuid(), _ -> new PlayerState());
        state.addLine(trimmed);

        ensureDisplaySpawned(player, state);
        refreshDisplayText(state);
        updateDisplayPosition(player, state);

        // Expire this specific line after TTL.
        Task task = MinecraftServer.getSchedulerManager()
                .buildTask(() -> expireLine(player.getUuid(), state, trimmed))
                .delay(TaskSchedule.duration(MESSAGE_TTL))
                .schedule();
        state.expiryTasks.add(task);
    }

    public void onPlayerMove(Player player) {
        if (player == null || player.isRemoved()) return;
        PlayerState state = states.get(player.getUuid());
        if (state == null || state.display == null || state.display.isRemoved()) return;
        updateDisplayPosition(player, state);
    }

    public void onPlayerDisconnect(Player player) {
        if (player == null) return;
        PlayerState state = states.remove(player.getUuid());
        if (state == null) return;
        state.cleanup();
    }

    private void expireLine(UUID playerUuid, PlayerState state, String line) {
        // Player might have disconnected / been cleaned already.
        PlayerState current = states.get(playerUuid);
        if (current != state) return;

        // Remove one matching occurrence, oldest-first (which matches our deque ordering).
        boolean removed = state.lines.removeFirstOccurrence(line);
        if (!removed) return;

        if (state.lines.isEmpty()) {
            state.removeDisplay();
            states.remove(playerUuid, state);
            return;
        }

        // Text needs to be refreshed immediately; position will be kept in sync by move events.
        refreshDisplayText(state);
    }

    private void ensureDisplaySpawned(Player player, PlayerState state) {
        if (state.display != null && !state.display.isRemoved()) {
            // Re-spawn if it got de-instanced.
            if (state.display.getInstance() == null && player.getInstance() != null) {
                state.display.setInstance(player.getInstance(), bubblePos(player));
            }
            return;
        }

        Instance instance = player.getInstance();
        if (instance == null) return;

        Entity display = new Entity(EntityType.TEXT_DISPLAY);

        TextDisplayMeta meta = (TextDisplayMeta) display.getEntityMeta();
        meta.setHasNoGravity(true);
        meta.setSeeThrough(false);
        meta.setShadow(false);
        meta.setBackgroundColor(0xE61A1A1A);
        meta.setLineWidth(200); // plenty for short messages
        meta.setScale(new Vec(0.9f, 0.9f, 0.9f));

        // Make sure the display doesn't collide / interact.
        display.setAutoViewable(true);

        state.display = display;
        display.setInstance(instance, bubblePos(player));
    }

    private void refreshDisplayText(PlayerState state) {
        if (state.display == null || state.display.isRemoved()) return;

        Component combined = Component.empty();

        int i = 0;
        for (String line : state.lines) {
            if (i++ > 0) combined = combined.append(Component.newline());

            combined = combined.append(
                    Component.text(PAD_X, NamedTextColor.WHITE)
                            .append(Component.text(line, NamedTextColor.WHITE))
                            .append(Component.text(PAD_X, NamedTextColor.WHITE))
            );
        }

        TextDisplayMeta meta = (TextDisplayMeta) state.display.getEntityMeta();
        meta.setText(combined);
    }


    private void updateDisplayPosition(Player player, PlayerState state) {
        if (state.display == null || state.display.isRemoved()) return;
        if (player.getInstance() == null) return;

        Pos target = bubblePos(player);

        // If the player changed instance, move the display too.
        if (state.display.getInstance() != player.getInstance()) {
            state.display.setInstance(player.getInstance(), target);
        } else {
            state.display.teleport(target);
        }
    }

    private Pos bubblePos(Player player) {
        Pos p = player.getPosition();
        return new Pos(p.x(), p.y() + BASE_Y_OFFSET, p.z(), p.yaw(), 0);
    }

    private static final class PlayerState {
        private final Deque<String> lines = new ArrayDeque<>(MAX_LINES);
        private final Deque<Task> expiryTasks = new ArrayDeque<>();
        private Entity display;

        void addLine(String line) {
            // We store oldest->newest; newest must be bottom, which matches rendering order.
            while (lines.size() >= MAX_LINES) {
                lines.removeFirst();
            }
            lines.addLast(line);
        }

        void removeDisplay() {
            if (display != null && !display.isRemoved()) {
                display.remove();
            }
            display = null;
        }

        void cleanup() {
            for (Task task : expiryTasks) {
                if (task != null) task.cancel();
            }
            expiryTasks.clear();
            lines.clear();
            removeDisplay();
        }
    }
}
