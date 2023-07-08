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
            String msg = String.format(
                    "Player shop set for %d %s at %.2f",
                    IntegerArgumentType.getInteger(context, "quantity"),
                    item.asString(),
                    DoubleArgumentType.getDouble(context, "price")
            );
            context.getSource().sendFeedback(Text.literal(msg).formatted(Formatting.GREEN), true);

            HologramBuilderHandler hologramBuilderHandler = new HologramBuilderHandler();
            WorldHologram worldHologram = hologramBuilderHandler.HologramBuild(context, blockPos);

            ShopLoader shopLoader = new ShopLoader(worldHologram, player, item.getItem(), DoubleArgumentType.getDouble(context, "price"), IntegerArgumentType.getInteger(context, "quantity"));
            ((LinkedList<ShopLoader>)ShopLoader.allShops).addLast(shopLoader);

            System.out.printf("Accepting %s's request to setup their shop\n", player.getName().getString());
            CommonShopData.removeByPlayer(player);
        }
    }

}
