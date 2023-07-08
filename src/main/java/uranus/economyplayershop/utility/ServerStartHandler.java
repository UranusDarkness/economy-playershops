package uranus.economyplayershop.utility;

import eu.pb4.holograms.api.holograms.WorldHologram;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import uranus.economyplayershop.common.CommonShopData;
import uranus.economyplayershop.common.HologramBuilderHandler;
import uranus.economyplayershop.common.ShopLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class ServerStartHandler implements ServerTickEvents.EndTick {
    public MinecraftServer server = null;

    private ServerPlayerEntity player;

    public void restoreShops() throws SQLException {
        Connection db = CommonShopData.DB;
        Statement stmt = db.createStatement();

        String sql = "SELECT *" +
                "FROM shop;";
        ResultSet res = stmt.executeQuery(sql);
        while (res.next()) {
            try {
                byte[] hologramBytes = res.getBytes("hologramBytes");
                ByteArrayInputStream bais = new ByteArrayInputStream(hologramBytes);
                ObjectInputStream ois = new ObjectInputStream(bais);
                WorldHologram hologram = (WorldHologram) ois.readObject();
                hologram.show();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
//            String ownerString = res.getString("owner");
//            UUID uuid = UUID.fromString(ownerString);
//
//
//            String dbItem = res.getString("item_id");
//            String[] itemFields = dbItem.split(":");
//            String mod = itemFields[0];
//            String itemName = itemFields[1];
//            Identifier itemId = new Identifier(mod, itemName);
//            Item item = Registry.ITEM.get(itemId);
//
//            int quantity = res.getInt("quantity");
//            double price = res.getDouble("price");
//            float x = res.getFloat("x_coord");
//            float y = res.getFloat("y_coord");
//            float z = res.getFloat("z_coord");
//            BlockPos coords = new BlockPos(x, y, z);

//            restoreShop(player, item, quantity, price, coords);
        }
    }

    private void restoreShop(ServerPlayerEntity player, Item item, int quantity, double price, BlockPos coords) {
        String msg = String.format(
                "Player shop restored for %d %s at %.2f",
                quantity,
                item.getName().getString(),
                price
        );
        System.out.println(msg);

        HologramBuilderHandler hologramBuilderHandler = new HologramBuilderHandler();
        WorldHologram worldHologram = hologramBuilderHandler.hologramRestore(player, coords, item, quantity, price);

        ShopLoader shopLoader = new ShopLoader(worldHologram, player, item, price, quantity);
        ((LinkedList<ShopLoader>)ShopLoader.allShops).addLast(shopLoader);
    }

    @Override
    public void onEndTick(MinecraftServer server) {
        if (this.server == null) {
            this.server = server;
            try {
                restoreShops();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
