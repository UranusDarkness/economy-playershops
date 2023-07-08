package uranus.economyplayershop.db;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import uranus.economyplayershop.common.CommonShopData;
import uranus.economyplayershop.common.HologramBuilderHandler;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class ShopDbInterface {

    public static boolean addShopToDb(byte[] hologramBytes) throws SQLException {
        ByteArrayInputStream bais = new ByteArrayInputStream(hologramBytes);

        String sql = "INSERT INTO shop(hologram) VALUES (:hologram);";
        PreparedStatement stmt = CommonShopData.DB.prepareStatement(sql);
        stmt.setBinaryStream(1, bais, hologramBytes.length);

        return stmt.execute();
    }

    public static boolean removeShopFromDb(int shopId) throws SQLException {
        String sql = "DELETE FROM shop WHERE shop_id = :id";
        PreparedStatement stmt = CommonShopData.DB.prepareStatement(sql);
        stmt.setInt(1, shopId);

        return stmt.execute();
    }

}
