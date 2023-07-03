package uranus.economyplayershop.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import uranus.economyplayershop.common.CommonShopData;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.mojang.brigadier.arguments.DoubleArgumentType.getDouble;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static net.minecraft.command.argument.ItemStackArgumentType.getItemStackArgument;


public class PlayerShopCommand {
    //static int pos = -1;
    //static int pos2 = 1;
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

        System.out.println("Adding player request for shop configuration to list");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            context.getSource().sendFeedback(Text.literal("Your request to set up a shop has expired.").formatted(Formatting.RED), false);
            CommonShopData.removeByPlayer(player);
        }, SHOP_CONFIG_TIMEOUT, TimeUnit.SECONDS);
        System.out.println("Scheduling removal in 10 seconds/on chest click");

        CommonShopData.addRequest(player, future, context);
        return 1;
        /*ServerPlayerEntity playerEntity = context.getSource().getPlayer();

        WorldHologram hologram = new WorldHologram(playerEntity.getWorld(), playerEntity.getPos());
        hologram.addText(Text.literal("prova1"));
        hologram.addElement(new EntityHologramElement(getEntityType(false).create(playerEntity.world)));
        hologram.addItemStack(Items.ACACIA_BOAT.getDefaultStack(), false);
        hologram.addItemStack(Items.DIAMOND_BLOCK.getDefaultStack(), true);
        hologram.addText(Text.literal("« »"));
        hologram.addElement(new CubeHitboxHologramElement(2, new Vec3d(0, -0.2, 0)) {
            @Override
            public void onClick(AbstractHologram hologram, ServerPlayerEntity player, InteractionType type, @Nullable Hand hand, @Nullable Vec3d vec, int entityId) {
                super.onClick(hologram, player, type, hand, vec, entityId);
                hologram.setElement(1, new EntityHologramElement(getEntityType(type == InteractionType.ATTACK).create(playerEntity.world)));
            }
        });
        hologram.addText(Text.literal("434234254234562653247y4575678rt").formatted(Formatting.AQUA));

        hologram.show();
        context.getSource().sendMessage(Text.literal("player shop set"));
        return 1;*/
    }

    /*private static EntityType getEntityType(boolean previous) {
        if (previous) {
            pos--;
        } else {
            pos++;
        }
        EntityType type = Registry.ENTITY_TYPE.get(pos);

        if (type == null) {
            pos = 0;
            type = Registry.ENTITY_TYPE.get(pos);
        }

        System.out.println(type);

        return type;
    }*/

}
