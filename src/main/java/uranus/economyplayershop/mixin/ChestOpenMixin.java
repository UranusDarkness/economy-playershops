package uranus.economyplayershop.mixin;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.holograms.api.holograms.WorldHologram;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uranus.economyplayershop.common.CommonShopData;
import uranus.economyplayershop.common.HologramBuilderHandler;
import uranus.economyplayershop.common.ShopLoader;
import uranus.economyplayershop.common.ShopRequest;
import uranus.economyplayershop.db.ShopDbInterface;

import java.io.*;
import java.sql.SQLException;
import java.util.LinkedList;

@Mixin(ChestBlockEntity.class)
public class ChestOpenMixin extends BlockEntity {

    public ChestOpenMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "onOpen", at = @At("HEAD"))
    protected void injectOnOpen(PlayerEntity player, CallbackInfo ci){
        BlockPos blockPos = this.getPos();

        ShopRequest playerRequest = CommonShopData.getByPlayer(player);
        if (playerRequest != null) {
            CommandContext<ServerCommandSource> context = playerRequest.getPlayerContext();

            ItemStackArgument item = ItemStackArgumentType.getItemStackArgument(context, "item");

            String itemName = item.asString();
            int quantity = IntegerArgumentType.getInteger(context, "quantity");
            double price = DoubleArgumentType.getDouble(context, "price");

            String msg = String.format(
                    "Player shop set for %d %s at %.2f",
                    quantity,
                    itemName,
                    price
            );

            context.getSource().sendFeedback(Text.literal(msg).formatted(Formatting.GREEN), true);

            HologramBuilderHandler hologramBuilderHandler = new HologramBuilderHandler();
            WorldHologram worldHologram = hologramBuilderHandler.HologramBuild(context, blockPos);

            ShopLoader shopLoader = new ShopLoader(worldHologram, player, item.getItem(), DoubleArgumentType.getDouble(context, "price"), IntegerArgumentType.getInteger(context, "quantity"));
            ((LinkedList<ShopLoader>)ShopLoader.allShops).addLast(shopLoader);

            System.out.printf("Accepting %s's request to setup their shop\n", player.getName().getString());

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream os = new ObjectOutputStream(baos);
                os.writeObject(worldHologram);
                byte[] hologramBytes = baos.toByteArray();

                if (ShopDbInterface.addShopToDb(hologramBytes))
                    System.out.printf("Added %s's %s shop", player.getEntityName(), itemName);
                else
                    System.out.printf("Could not add %s's %s shop", player.getEntityName(), itemName);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            CommonShopData.removeByPlayer(player);
        }
    }

}
