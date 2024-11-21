package club.cybercraftman.leek.creeper;

import club.cybercraftman.leek.common.dto.event.creep.CreepEvent;

public interface ICreeper {

    boolean isSupport(CreepEvent event);

    void creep();


}
