package uranus.economyplayershop;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uranus.economyplayershop.command.AdminPlayerShopCommand;
import uranus.economyplayershop.command.PlayerShopCommand;

public class PlayerShopMain implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("economy-player-shop");

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			if (environment.dedicated) {

				LiteralCommandNode<ServerCommandSource> adminPlayerShopNode = CommandManager.literal("adminplayershop").build();
				LiteralCommandNode<ServerCommandSource> adminSetNode = CommandManager.literal("set").executes(AdminPlayerShopCommand::set).build();

				LiteralCommandNode<ServerCommandSource> playerShopNode = CommandManager.literal("playershop").executes(PlayerShopCommand::base).build();
				LiteralCommandNode<ServerCommandSource> setNode = CommandManager.literal("set")
						.then(CommandManager.argument("item", ItemStackArgumentType.itemStack(registryAccess)).executes(PlayerShopCommand::usage)
								.then(CommandManager.argument("quantity", IntegerArgumentType.integer()).executes(PlayerShopCommand::usage)
										.then(CommandManager.argument("price", DoubleArgumentType.doubleArg(0.0)).executes(PlayerShopCommand::set))))

						.executes(PlayerShopCommand::usage).build();

				dispatcher.getRoot().addChild(adminPlayerShopNode);
				adminPlayerShopNode.addChild(adminSetNode);

				dispatcher.getRoot().addChild(playerShopNode);
				playerShopNode.addChild(setNode);

			}
		});
	}
}