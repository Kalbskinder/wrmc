package net.kalbskinder;

import net.kalbskinder.entities.RallyCar;
import net.kalbskinder.events.EventManager;
import net.kalbskinder.systems.npc.InteractableNpc;
import net.kalbskinder.systems.npc.Npc;
import net.kalbskinder.systems.npc.NpcLookSystem;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityPose;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.client.play.ClientInputPacket;

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

        // Start NPC systems
        NpcLookSystem.start();

        // Set the ChunkGenerator
        instance.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));

        // Start the server on port 25565
        minecraftServer.start("0.0.0.0", 25565);


        // Summon test npc
        Npc npc = new Npc("Obama", "<red><bold>DON'T CLICK<reset>", "7f912e2c-46fd-4c14-b7d0-376cd00ba373", new Pos(0.0, 40.0, 0.0), true, instance);
        UUID npcUuid = npc.spawn();
        npc.setPose(EntityPose.SLEEPING);

        InteractableNpc npc1 = new InteractableNpc("Interactable", "<yellow><bold>CLICK<reset>", "b956ded3-4384-4f1a-b883-bd50f5746043", new Pos(3.0, 40.0, 2.0), true, instance);
        npc1.spawn();
        npc1.setOnRightClick(player -> {
            player.sendMessage("You right-clicked the NPC!");
        });

        npc1.setOnLeftClick(player  -> {
            player.sendMessage("You left-clicked the NPC!");
        });

        // Spawn test rally car (currently just a boat entity).
        RallyCar rallyCar = new RallyCar();
        rallyCar.setInstance(instance, new Pos(5.0, 40.0, 0.0));

        MinecraftServer.getPacketListenerManager()
                .setListener(ClientInputPacket.class, (packet, player) -> {

                    Entity vehicle = player.getVehicle();
                    if (!(vehicle instanceof RallyCar car)) return;

                    boolean forward = packet.forward();
                    boolean backward = packet.backward();
                    boolean left = packet.left();
                    boolean right = packet.right();
                    boolean handbrake = packet.jump(); // future handbrake implementation

                    car.setInput(forward, backward, left, right, handbrake);
                });

    }
}
