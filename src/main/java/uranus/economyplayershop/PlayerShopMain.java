package uranus.economyplayershop;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uranus.economyplayershop.command.AdminPlayerShopCommand;
import uranus.economyplayershop.command.PlayerShopCommand;
import uranus.economyplayershop.common.CommonShopData;
import uranus.economyplayershop.config.ConfigData;
import uranus.economyplayershop.config.ConfigManager;
import uranus.economyplayershop.utility.ServerStartHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class PlayerShopMain implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("economy-player-shop");

	public static ServerStartHandler startHandler;

	@Override
	public void onInitialize() {
		startHandler = new ServerStartHandler();
		ServerTickEvents.END_SERVER_TICK.register(startHandler);
		ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStop);
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			if (environment.dedicated) {

				LiteralCommandNode<ServerCommandSource> adminPlayerShopNode = CommandManager.literal("adminplayershop").build();
				LiteralCommandNode<ServerCommandSource> adminSetNode = CommandManager.literal("set").executes(AdminPlayerShopCommand::set).build();

				LiteralCommandNode<ServerCommandSource> playerShopNode = CommandManager.literal("playershop").executes(PlayerShopCommand::base).build();
				LiteralCommandNode<ServerCommandSource> setNode = CommandManager.literal("set")
						.then(CommandManager.argument("item", ItemStackArgumentType.itemStack(registryAccess)).executes(PlayerShopCommand::usage)
								.then(CommandManager.argument("quantity", IntegerArgumentType.integer(1)).executes(PlayerShopCommand::usage)
										.then(CommandManager.argument("price", DoubleArgumentType.doubleArg(0.0)).executes(PlayerShopCommand::set))))

						.executes(PlayerShopCommand::usage).build();

				dispatcher.getRoot().addChild(adminPlayerShopNode);
				adminPlayerShopNode.addChild(adminSetNode);

				dispatcher.getRoot().addChild(playerShopNode);
				playerShopNode.addChild(setNode);
			}
		});

		if (!ConfigManager.loadConfig()) {

			throw new RuntimeException("Couldn't create database configuration. Exiting...");
		}
		initializeDatabase();
	}

	public void initializeDatabase(){
		Connection db = null;
		try {
			ConfigData configData = new ConfigData();
			String conn = "jdbc:sqlite:"+configData.sqliteDatabaseLocation;
			db = DriverManager.getConnection(conn);
			if (db == null)
				throw new RuntimeException("Couldn't establish connection with the database. Exiting...");
			System.out.println("Connection to SQLite has been established.");
			CommonShopData.DB = db;

			// Create necessary tables
			String sql = "CREATE TABLE IF NOT EXISTS player(" +
						"uuid VARCHAR(36) PRIMARY KEY," +
						"username VARCHAR(16)" +
					");" +
					"CREATE TABLE IF NOT EXISTS shop(" +
						"shop_id INTEGER PRIMARY KEY AUTOINCREMENT," +
						"hologram BLOB" +
					");";

			Statement stmt = db.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void onServerStop(MinecraftServer server) {
		try {
			if (CommonShopData.DB != null) {
				CommonShopData.DB.close();
			}
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
	}
}