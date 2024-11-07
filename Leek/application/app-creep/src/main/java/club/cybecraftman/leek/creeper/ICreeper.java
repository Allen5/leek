package club.cybecraftman.leek.creeper;

import club.cybecraftman.leek.common.dto.event.creep.CreepEvent;

public interface ICreeper {

    boolean isSupport(CreepEvent event);

    void creep();


}
