package uranus.economyplayershop.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class AdminPlayerShopCommand {
    public static int set(CommandContext<ServerCommandSource> context) {
        context.getSource().sendMessage(Text.literal("Admin player shop set"));
        return 1;
    }
}
