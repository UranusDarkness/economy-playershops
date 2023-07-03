package uranus.economyplayershop.common;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledFuture;

public class CommonShopData {
    final static private List<ShopRequest> requests = new ArrayList<>();

    public static ShopRequest getByPlayer(PlayerEntity player) {
        if (player == null)
            return null;
        UUID uuid = player.getUuid();
        for (ShopRequest request: requests) {
            if (request.getRequestingPlayer().getUuid().equals(uuid)) {
                return request;
            }
        }
        return null;
    }

    public static boolean removeByPlayer(PlayerEntity player) {
        ShopRequest sr = getByPlayer(player);
        if (sr != null) {
            ScheduledFuture<?> sf = sr.getscheduledFuture();
            if (!sf.isDone()) {
                System.out.printf("Shutting down %s's request to configure a shop\n", player.getName().getString());
                sf.cancel(false);
            }
            System.out.printf("Removing %s's request to configure a shop from the list\n", player.getName().getString());
            return requests.remove(sr);
        }
        return false;
    }

    public static boolean addRequest(PlayerEntity player, ScheduledFuture<?> future, CommandContext<ServerCommandSource> playerContext) {
        ShopRequest sr = new ShopRequest(player, future, playerContext);
        System.out.printf("Adding %s's request to configure a shop from the list\n", player.getName().getString());
        return requests.add(sr);
    }
}
