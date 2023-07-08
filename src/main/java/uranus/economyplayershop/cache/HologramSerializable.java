package uranus.economyplayershop.cache;

import eu.pb4.holograms.api.holograms.WorldHologram;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.Serializable;

public class HologramSerializable implements Serializable {
    private final WorldHologram hologram;

    public HologramSerializable(WorldHologram hologram) {
        this.hologram = hologram;
    }
}
