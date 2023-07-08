package uranus.economyplayershop.common;

import eu.pb4.holograms.api.holograms.WorldHologram;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;

import java.util.LinkedList;
import java.util.List;

public class ShopLoader {
    int id;
    WorldHologram hologram;
    PlayerEntity owner;
    Item item;
    double price;
    int quantity;
    public static final List<ShopLoader> allShops = new LinkedList<ShopLoader>();

    public ShopLoader(WorldHologram hologram, PlayerEntity owner, Item item, double price, int quantity) {
        this.hologram = hologram;
        this.owner = owner;
        this.item = item;
        this.price = price;
        this.quantity = quantity;
    }
}
