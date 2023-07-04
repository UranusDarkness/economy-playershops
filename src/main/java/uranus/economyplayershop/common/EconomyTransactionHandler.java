package uranus.economyplayershop.common;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.the_fireplace.annotateddi.api.Injectors;
import dev.the_fireplace.grandeconomy.api.injectables.Economy;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;


public class EconomyTransactionHandler {
    Economy economy = Injectors.INSTANCE.getAutoInjector("grandeconomy").getInstance(Economy.class);
    public int buyFromPlayerShop(CommandContext<ServerCommandSource> context, ServerPlayerEntity seller, ServerPlayerEntity buyer, double price, Item item, int quantity){

        System.out.println("prima: "+economy.getBalance(buyer.getUuid(), true));

        if(economy.getBalance(buyer.getUuid(), true) >= price){
            economy.takeFromBalance(buyer.getUuid(), DoubleArgumentType.getDouble(context, "price"), true);
            System.out.println("dopo acquisto: "+economy.getBalance(buyer.getUuid(), true));
            economy.addToBalance(seller.getUuid(), DoubleArgumentType.getDouble(context, "price"), true);
            System.out.println("fine accredito: "+economy.getBalance(buyer.getUuid(), true));
            buyer.getInventory().insertStack(new ItemStack(item, quantity));
            return 1;
        }
        else {
            return -1;
        }

    }
}
