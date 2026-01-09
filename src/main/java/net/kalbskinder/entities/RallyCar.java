package net.kalbskinder.entities;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;

public class RallyCar extends Entity {

    private Vec velocity = Vec.ZERO;
    private float yaw;           // car direction
    private float steering;      // -1..1
    private float throttle;      // -1..1

    // Tuning values
    private static final double ENGINE_FORCE = 0.08;
    private static final double BRAKE_FORCE = 0.12;
    private static final double MAX_SPEED = 1.2;
    private static final double FRICTION = 0.96;
    private static final double DRIFT_FACTOR = 0.85;

    public RallyCar() {
        super(EntityType.ARMOR_STAND);
        setNoGravity(true);
        editEntityMeta(ArmorStandMeta.class, meta -> {
            meta.setSmall(true);
        });
    }

    public void setInput(boolean forward, boolean backward, boolean left, boolean right) {
        throttle = forward ? 1 : backward ? -1 : 0;
        steering = left ? -1 : right ? 1 : 0;
    }

    @Override
    public void tick(long time) {
        // If we're not spawned yet, do nothing.
        if (getInstance() == null) return;

        // Steering changes yaw
        yaw += steering * 2.5f;

        // Forward direction
        Vec forward = new Vec(
                -Math.sin(Math.toRadians(yaw)),
                0,
                Math.cos(Math.toRadians(yaw))
        );

        // Acceleration / braking
        if (throttle != 0) {
            velocity = velocity.add(forward.mul(ENGINE_FORCE * throttle));
        } else {
            // mild braking when no throttle (uses BRAKE_FORCE as a damping term)
            velocity = velocity.mul(1.0 - (BRAKE_FORCE * 0.25));
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

        // Friction
        velocity = velocity.mul(FRICTION);

        // Move entity inside the same instance
        teleport(getPosition().add(velocity));
        setView(yaw, 0);
    }

}
