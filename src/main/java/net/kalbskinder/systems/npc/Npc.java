package net.kalbskinder.systems.npc;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityPose;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.avatar.MannequinMeta;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.network.player.ResolvableProfile;

import java.util.Set;
import java.util.UUID;


public class Npc {
    // Lombok may not be enabled in every build environment; keep explicit accessors for core fields.
    private UUID uuid;
    private Entity entity;

    private final InstanceContainer instance;
    private final String name;
    private final String description;
    private final String skinUuid;

    private Pos pos;
    private final boolean lookAtPlayers;

    public InstanceContainer getInstance() {
        return instance;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSkinUuid() {
        return skinUuid;
    }

    public Pos getPos() {
        return pos;
    }

    public void setPos(Pos pos) {
        this.pos = pos;
    }

    public boolean isLookAtPlayers() {
        return lookAtPlayers;
    }

    private final Set<EntityPose> allowedPoses = Set.of(EntityPose.SNEAKING, EntityPose.STANDING, EntityPose.SLEEPING, EntityPose.SWIMMING, EntityPose.FALL_FLYING);
    private final NpcManager npcManager = NpcManager.getInstance();
    private final MiniMessage mm = MiniMessage.miniMessage();

    public Npc(
            String name,
            String description,
            String skinUuid,
            Pos pos,
            boolean lookAtPlayers,
            InstanceContainer instance
    )
    {
        this.name = name;
        this.description = description;
        this.skinUuid = skinUuid;
        this.pos = pos;
        this.lookAtPlayers = lookAtPlayers;
        this.instance = instance;
    }

    public UUID spawn() {
        Entity npc = new Entity(EntityType.MANNEQUIN);
        this.entity = npc;
        this.uuid = npc.getUuid();
        npc.setInstance(instance, pos);
        npc.setNoGravity(true);

        npc.editEntityMeta(MannequinMeta.class, meta -> {
            // Set a skin profile from username or UUID
            PlayerSkin skin = PlayerSkin.fromUuid(skinUuid); // fetches texture & signature
            ResolvableProfile profile = new ResolvableProfile(skin);

            meta.setProfile(profile);

            // Example: custom name
            meta.setCustomName(mm.deserialize(name));
            meta.setCustomNameVisible(true);

            meta.setDescription(mm.deserialize(description));
        });

        npcManager.addNpc(this);
        return npc.getUuid();
    }

    public void setPose(EntityPose pose) {
        if (allowedPoses.contains(pose)) {
            this.entity.setPose(pose);
        }
        npcManager.updateNpc(this);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
