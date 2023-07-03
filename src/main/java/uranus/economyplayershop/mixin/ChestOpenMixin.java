package uranus.economyplayershop.mixin;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
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
import uranus.economyplayershop.common.ShopRequest;

@Mixin(ChestBlockEntity.class)
public class ChestOpenMixin extends BlockEntity {

    public ChestOpenMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "onOpen", at = @At("HEAD"))
    protected void injectOnOpen(PlayerEntity player, CallbackInfo ci){
        BlockPos blockPos = this.getPos();
        //System.out.println(String.format("x: %d y: %d z: %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()));

        ShopRequest playerRequest = CommonShopData.getByPlayer(player);
        if (playerRequest != null) {
            CommandContext<ServerCommandSource> context = playerRequest.getPlayerContext();
            ItemStackArgument item = ItemStackArgumentType.getItemStackArgument(context, "item");
            String msg = String.format(
                    "PlayerShop settato con %s %d %.2f",
                    item.asString(),
                    IntegerArgumentType.getInteger(context, "quantity"),
                    DoubleArgumentType.getDouble(context, "price")
            );
            context.getSource().sendFeedback(Text.literal(msg).formatted(Formatting.GREEN), true);

            System.out.printf("Accepting %s's request to setup their shop\n", player.getName().getString());
            CommonShopData.removeByPlayer(player);
        }
    }

}
