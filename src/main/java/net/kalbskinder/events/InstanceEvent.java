package net.kalbskinder.events;

import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.instance.InstanceContainer;

public class InstanceEvent extends BasicEvent {
    
    private final InstanceContainer instance;
    
    public InstanceEvent(GlobalEventHandler eventHandler, InstanceContainer instance) {
        super(eventHandler);
        this.instance = instance;
    }
    
    public InstanceContainer getInstance() {
        return instance;
    }
}
