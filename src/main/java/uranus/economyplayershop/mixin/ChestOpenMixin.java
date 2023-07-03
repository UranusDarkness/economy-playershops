package uranus.economyplayershop.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
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
        System.out.println(String.format("x: %d y: %d z: %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()));

        ShopRequest playerRequest = CommonShopData.getByPlayer(player);
        if (playerRequest != null) {
            System.out.printf("Accepting %s's request to setup their shop\n", player.getName().getString());
            CommonShopData.removeByPlayer(player);
        }
    }

}
