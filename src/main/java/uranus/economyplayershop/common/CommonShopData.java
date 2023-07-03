package uranus.economyplayershop.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class CommonShopData {
    final static private List<ShopRequest> requests = new ArrayList<>();

    public static ShopRequest getByPlayer(PlayerEntity player) {
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
            ExecutorService es = sr.getExecutorService();
            if (!es.isShutdown()) {
                System.out.printf("Shutting down %s's request to configure a shop\n", player.getName().getString());
                es.shutdown();
            }
            System.out.printf("Removing %s's request to configure a shop from the list\n", player.getName().getString());
            return requests.remove(sr);
        }
        return false;
    }

    public static boolean addRequest(PlayerEntity player, ExecutorService executor) {
        ShopRequest sr = new ShopRequest(player, executor);
        System.out.printf("Adding %s's request to configure a shop from the list\n", player.getName().getString());
        return requests.add(sr);
    }
}
