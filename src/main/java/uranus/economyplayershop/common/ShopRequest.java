package uranus.economyplayershop.common;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledFuture;

public class ShopRequest {
    /**
     * Player who requested to configure a shop
     */
    private PlayerEntity requestingPlayer;

    /**
     * The timer of the player's request
     */
    private ScheduledFuture<?> scheduledFuture;

    /**
     * The context in which the player is requesting access (the chat on which to show feedback)
     */
    private final CommandContext<ServerCommandSource> playerContext;

    public ShopRequest(PlayerEntity requestingPlayer, ScheduledFuture<?> scheduledFuture, CommandContext<ServerCommandSource> playerContext) {
        this.requestingPlayer = requestingPlayer;
        this.scheduledFuture = scheduledFuture;
        this.playerContext = playerContext;
    }

    public PlayerEntity getRequestingPlayer() {
        return requestingPlayer;
    }

    public ScheduledFuture<?> getscheduledFuture() {
        return scheduledFuture;
    }

    public CommandContext<ServerCommandSource> getPlayerContext() {
        return playerContext;
    }
}
