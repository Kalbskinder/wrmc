package net.kalbskinder.systems.npc;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.TaskSchedule;

/**
 * Periodically rotates NPC entities toward the nearest player in range.
 */
public final class NpcLookSystem {

    private static final double LOOK_RADIUS_BLOCKS = 8.0;
    private static final double LOOK_RADIUS_SQUARED = LOOK_RADIUS_BLOCKS * LOOK_RADIUS_BLOCKS;

    private NpcLookSystem() {
    }

    public static void start() {
        MinecraftServer.getSchedulerManager()
                .buildTask(NpcLookSystem::tick)
                // every tick keeps it smooth; easy to adjust later
                .repeat(TaskSchedule.tick(1))
                .schedule();
    }

    private static void tick() {
        for (NPC npc : NPCManager.getInstance().getAllNpcs()) {
            if (!npc.isLookAtPlayers()) continue;

            Entity entity = npc.getEntity();
            if (entity == null || entity.isRemoved()) continue;

            Instance instance = entity.getInstance();
            if (instance == null) continue;

            Player closest = null;
            double closestDistSq = LOOK_RADIUS_SQUARED;

            for (Player player : instance.getPlayers()) {
                if (player.isRemoved()) continue;

                double distSq = player.getPosition().distanceSquared(entity.getPosition());
                if (distSq <= closestDistSq) {
                    closest = player;
                    closestDistSq = distSq;
                }
            }

            if (closest != null) {
                entity.lookAt(closest);
            }
        }
    }
}
