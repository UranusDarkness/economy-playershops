package uranus.economyplayershop.common;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.the_fireplace.annotateddi.api.Injectors;
import dev.the_fireplace.grandeconomy.api.injectables.Economy;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;


public class EconomyTransactionHandler {
    Economy economy = Injectors.INSTANCE.getAutoInjector("grandeconomy").getInstance(Economy.class);
    public int buyFromPlayerShop(ServerPlayerEntity seller, ServerPlayerEntity buyer, double price, Item item, int quantity, BlockPos chestPos){

        if(economy.getBalance(buyer.getUuid(), true) >= price){

            ChestBlockEntity shop = (ChestBlockEntity) seller.getWorld().getBlockEntity(chestPos);

            int result = getItemStackFromChest(shop, item, quantity);

            if(result >= 0){
                //remove from buyer's balance
                economy.takeFromBalance(buyer.getUuid(), price, true);
                //add to seller's balance
                economy.addToBalance(seller.getUuid(), price, true);
                //add to buyer's inventory
                buyer.getInventory().offerOrDrop(new ItemStack(item, quantity));
                return 1;
            }
            else
                return -1;
        }
        else {
            return -1;
        }
    }

    public int getItemStackFromChest(ChestBlockEntity chest, Item itemToRemove, int quantity){
        int i = 0;
        if(chest.count(itemToRemove) >= quantity) {
            //loop to remove items from chest
            while(quantity > 0){
                if(chest.getStack(i).getItem().equals(itemToRemove)){
                    if(chest.getStack(i).getCount() == quantity){
                        chest.removeStack(i);
                        quantity = 0;
                    } else if (chest.getStack(i).getCount() > quantity) {
                        ItemStack newItem = new ItemStack(itemToRemove, chest.getStack(i).getCount() - quantity);
                        chest.removeStack(i);
                        chest.setStack(i, newItem);
                        quantity = 0;
                    } else if (chest.getStack(i).getCount() < quantity) {
                        quantity -= chest.getStack(i).getCount();
                        chest.removeStack(i, chest.getStack(i).getCount());
                    }
                }
                i++;
            }
            return 0;
        }
        else {
            return -1;
        }
    }
}
