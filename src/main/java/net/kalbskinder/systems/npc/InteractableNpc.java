package net.kalbskinder.systems.npc;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.avatar.MannequinMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.network.player.ResolvableProfile;

import java.util.UUID;

public class InteractableNpc extends Npc {
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final NpcManager npcManager = NpcManager.getInstance();
    private UUID interactionEntityUuid;

    private NpcClickAction onLeftClick;
    private NpcClickAction onRightClick;
    private boolean redirectClicks = false;

    public InteractableNpc(
            String name,
            String description,
            String skinUUID,
            Pos pos,
            boolean lookAtPlayers,
            InstanceContainer instance
    ) {
        super(name, description, skinUUID, pos, lookAtPlayers, instance);
    }

    /**
     * If one of the two actions is null and redirectClicks is set to true, the same action will be triggered in both occasions
     */
    public void setRedirectClicks(boolean redirect) {
        this.redirectClicks = redirect;
    }

    @Override
    public UUID spawn() {
        Entity npc = new Entity(EntityType.MANNEQUIN);
        setEntity(npc);
        setUuid(npc.getUuid());
        npc.setInstance(super.getInstance(), super.getPos());
        npc.setNoGravity(true);

        npc.editEntityMeta(MannequinMeta.class, meta -> {
            // Set a skin profile from username or UUID
            PlayerSkin skin = PlayerSkin.fromUuid(getSkinUuid()); // fetches texture & signature
            ResolvableProfile profile = new ResolvableProfile(skin);

            meta.setProfile(profile);

            meta.setCustomName(mm.deserialize(getName()));
            meta.setCustomNameVisible(true);

            meta.setDescription(mm.deserialize(getDescription()));
        });

        // Interaction
        Entity interactionEntity = new Entity(EntityType.INTERACTION);
        this.interactionEntityUuid = interactionEntity.getUuid();
        interactionEntity.setNoGravity(true);
        interactionEntity.editEntityMeta(InteractionMeta.class, meta -> {
            meta.setWidth(0.9f);
            meta.setHeight(1.8f);
        });
        interactionEntity.setInstance(getInstance(), getPos());

        npcManager.addNpc(this);
        return npc.getUuid();
    }

    public UUID getInteractionEntityUuid() {
        return this.interactionEntityUuid;
    }
}
