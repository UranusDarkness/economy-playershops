package uranus.economyplayershop.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Timer;
import java.util.concurrent.ExecutorService;

public class ShopRequest {
    /**
     * Player who requested to configure a shop
     */
    private PlayerEntity requestingPlayer;

    /**
     * The timer of the player's request
     */
    private ExecutorService executor;

    public ShopRequest(PlayerEntity requestingPlayer, ExecutorService executor) {
        this.requestingPlayer = requestingPlayer;
        this.executor = executor;
    }

    public PlayerEntity getRequestingPlayer() {
        return requestingPlayer;
    }

    public ExecutorService getExecutorService() {
        return executor;
    }
}
