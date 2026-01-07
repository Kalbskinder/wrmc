package net.kalbskinder.systems.npc;

import net.minestom.server.entity.Player;

@FunctionalInterface
public interface NpcClickAction {
    void execute(Player player);
}
