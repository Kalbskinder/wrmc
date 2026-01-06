package net.kalbskinder;

import net.kalbskinder.events.EventManager;
import net.kalbskinder.systems.npc.NPC;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;

import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        // Initialization
        MinecraftServer minecraftServer = MinecraftServer.init();

        // Create the instance
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instance = instanceManager.createInstanceContainer();

        // Add lighting
        instance.setChunkSupplier(LightingChunk::new);

        // Register event manager
        EventManager eventManager = new EventManager(instance);
        eventManager.registerEvents();

        // Set the ChunkGenerator
        instance.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));

        // Start the server on port 25565
        minecraftServer.start("0.0.0.0", 25565);


        // Summon test npc
        NPC npc = new NPC("Test", "7f912e2c-46fd-4c14-b7d0-376cd00ba373", new Pos(0.0, 40.0, 0.0), false, instance);
        UUID npcUuid = npc.spawn();
    }
}
