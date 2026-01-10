package net.kalbskinder.entities;

import net.kyori.adventure.util.ARGBLike;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.metadata.monster.zombie.ZombieMeta;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.recipe.display.SlotDisplay;
import net.minestom.server.tag.Tag;

import java.util.List;

public class RallyCar extends EntityCreature {

    private Vec velocity = Vec.ZERO;
    private float yaw;           // car direction
    private float steering;      // -1..1
    private float throttle;      // -1..1
    private boolean braking;
    private boolean handbrake;

    // Tuning values
    private static final double ENGINE_FORCE = 0.05;
    private static final double BRAKE_FORCE = 0.07;
    private static final double MAX_SPEED = 1.2;
    private static final double FRICTION = 0.96;
    private static final double DRIFT_FACTOR = 0.85;
    private static final double MAX_STEER_ANGLE = 2.8; // degrees
    private static final double MIN_TURN_RATE = 0.9;    // high speed
    private static final double MAX_TURN_RATE = 2.8;    // low speed

    // Keep this very close to 1.0 for slow speed loss.
    private static final double COAST_FRICTION = 0.995;

    // TODO: adjust this when implementing drifting
    private static final double HANDBRAKE_FRICTION = 0.88;


    public RallyCar() {
        super(EntityType.ARMOR_STAND);
        setNoGravity(true);
        editEntityMeta(ArmorStandMeta.class, meta -> {
            meta.setSmall(true);
        });
        //ItemStack carModel = ItemStack.of(Material.STICK).withCustomModelData(List.of(1.0f), List.of(), List.of(), List.of());

        setEquipment(EquipmentSlot.HELMET, ItemStack.of(Material.STICK));
    }

    public void setInput(boolean forward, boolean backward, boolean left, boolean right, boolean handbrake) {
        throttle = forward ? 1 : 0;
        steering = left ? -1 : right ? 1 : 0;
        braking = backward;
        this.handbrake = handbrake;
    }

    @Override
    public void tick(long time) {
        // If we're not spawned yet, do nothing.
        if (getInstance() == null) return;

        double speed = velocity.length();

        // Steering effectiveness decreases with speed
        double speedFactor = 1.0 - Math.min(speed / MAX_SPEED, 1.0);

        // Turn rate interpolation
        double turnRate = MIN_TURN_RATE +
                (MAX_TURN_RATE - MIN_TURN_RATE) * speedFactor;

        // Desired steering angle
        double steerAngle = steering * MAX_STEER_ANGLE * turnRate;

        // Rotate velocity vector (this is the key)
        if (speed > 0.01 && steering != 0) {
            double radians = Math.toRadians(steerAngle);

            double cos = Math.cos(radians);
            double sin = Math.sin(radians);

            velocity = new Vec(
                    velocity.x() * cos - velocity.z() * sin,
                    0,
                    velocity.x() * sin + velocity.z() * cos
            );
        }

        // Forward direction
        Vec forward = new Vec(
                -Math.sin(Math.toRadians(yaw)),
                0,
                Math.cos(Math.toRadians(yaw))
        );

        // Acceleration
        if (throttle != 0) {
            velocity = velocity.add(forward.mul(ENGINE_FORCE * throttle));
        }

        // Braking
        if (braking && velocity.length() > 0.01) {
            Vec brakeDir = velocity.normalize().mul(-BRAKE_FORCE / 8);
            velocity = velocity.add(brakeDir);
        }

        // Clamp speed
        if (velocity.length() > MAX_SPEED) {
            velocity = velocity.normalize().mul(MAX_SPEED);
        }

        // Drift physics
        Vec forwardVel = forward.mul(velocity.dot(forward));
        Vec sidewaysVel = velocity.sub(forwardVel);

        // Reduce sideways velocity (drift control)
        velocity = forwardVel.add(sidewaysVel.mul(DRIFT_FACTOR));

        // Friction / drag
        if (throttle != 0) {
            velocity = velocity.mul(FRICTION);
        } else {
            velocity = velocity.mul(COAST_FRICTION);
        }

        if (handbrake) {
            velocity = velocity.mul(HANDBRAKE_FRICTION);
        }

        // Move entity inside the same instance
        teleport(getPosition().add(velocity));


        if (velocity.length() > 0.01) {
            yaw = (float) Math.toDegrees(
                    Math.atan2(-velocity.x(), velocity.z())
            );
        }

        setView(yaw, 0);
    }

}
