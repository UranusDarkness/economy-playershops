package uranus.economyplayershop.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import uranus.economyplayershop.common.CommonShopData;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class PlayerShopCommand {
    /**
     * Timeout in seconds after which shop config requests becomes stale
     */
    final static int SHOP_CONFIG_TIMEOUT = 10;

    public static int base(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(Text.literal("Economy PlayerShop by UranusDarkness & ShinusThePanther").formatted(Formatting.YELLOW), false);
        return 1;
    }

    public static int usage(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(Text.literal("Usage: /playershop set <item> <quantity> <price>").formatted(Formatting.YELLOW), false);
        return 1;
    }

    public static int set(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (CommonShopData.getByPlayer(player) != null) {
            context.getSource().sendFeedback(
                    Text.literal(
                            String.format("Please wait %d seconds between requests to set up another shop", SHOP_CONFIG_TIMEOUT)
                    ).formatted(Formatting.RED), false
            );
            return -1;
        }

        String msg = "Right click on your chest to set the player shop";
        context.getSource().sendFeedback(Text.literal(msg).formatted(Formatting.YELLOW), false);


        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            context.getSource().sendFeedback(Text.literal("Your request to set up a shop has expired.").formatted(Formatting.RED), false);
            CommonShopData.removeByPlayer(player);
        }, SHOP_CONFIG_TIMEOUT, TimeUnit.SECONDS);

        CommonShopData.addRequest(player, future, context);
        return 1;
    }

}
