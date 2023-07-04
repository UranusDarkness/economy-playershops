package uranus.economyplayershop.common;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.holograms.api.InteractionType;
import eu.pb4.holograms.api.elements.clickable.CubeHitboxHologramElement;
import eu.pb4.holograms.api.holograms.AbstractHologram;
import eu.pb4.holograms.api.holograms.WorldHologram;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;


public class HologramBuilderHandler {

    String msg;

    public void HologramBuilder(CommandContext<ServerCommandSource> context, BlockPos blockPos){
        ServerPlayerEntity playerEntity = context.getSource().getPlayer();
        //Entity entity = playerEntity;
        WorldHologram hologram = new WorldHologram(playerEntity.getWorld(), new Vec3d(blockPos.getX()+0.5, blockPos.getY()+1, blockPos.getZ()+0.5));
        //System.out.println(entity.getEntityName());
        if(playerEntity.getEntityName().endsWith("s"))
            msg = playerEntity.getEntityName() + "' shop";
        else
            msg = playerEntity.getEntityName() + "'s shop";
        hologram.addText(Text.literal(msg));

        Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
        hologram.addItemStack(item.getDefaultStack(), true);

        msg = String.format("%d", IntegerArgumentType.getInteger(context, "quantity"));
        hologram.addText(Text.literal(msg));

        msg = String.format("%.2f", DoubleArgumentType.getDouble(context, "price"));
        hologram.addText(Text.literal(msg+" $").formatted(Formatting.GREEN));

        hologram.addElement(new CubeHitboxHologramElement(2, new Vec3d(0, 0.1, 0)) {
            @Override
            public void onClick(AbstractHologram hologram, ServerPlayerEntity player, InteractionType type, @Nullable Hand hand, @Nullable Vec3d vec, int entityId) {
                super.onClick(hologram, player, type, hand, vec, entityId);
                EconomyTransactionHandler economyTransactionHandler = new EconomyTransactionHandler();

                int purchaseresult = economyTransactionHandler.buyFromPlayerShop(context, playerEntity, player, DoubleArgumentType.getDouble(context, "price"),
                        item, IntegerArgumentType.getInteger(context, "quantity"));

                if(purchaseresult >= 0){
                    player.sendMessage(Text.literal("Acquisto effettuato con successo!").formatted(Formatting.GREEN), false);

                    playerEntity.sendMessage(Text.literal(player.getEntityName()+
                            " ha comprato dal tuo shop. Ti sono stati accreditati " + msg + " $").formatted(Formatting.GREEN));

                }
                else {
                    player.sendMessage(Text.literal("Acquisto fallito!").formatted(Formatting.RED), false);
                }
                //hologram.setElement(1, new EntityHologramElement(getEntityType(type == InteractionType.ATTACK).create(playerEntity.world)));
            }
        });


        hologram.show();
    }
}
