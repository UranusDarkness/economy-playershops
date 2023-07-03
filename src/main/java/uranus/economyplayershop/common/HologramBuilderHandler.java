package uranus.economyplayershop.common;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.holograms.api.holograms.WorldHologram;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class HologramBuilderHandler {
    static int pos = -1;
    static int pos2 = 1;

    String msg;

    public void HologramBuilder(CommandContext<ServerCommandSource> context, BlockPos blockPos){
        ServerPlayerEntity playerEntity = context.getSource().getPlayer();
        Entity entity = playerEntity;
        WorldHologram hologram = new WorldHologram(playerEntity.getWorld(), new Vec3d(blockPos.getX()+0.5, blockPos.getY()+1, blockPos.getZ()+0.5));
        //System.out.println(entity.getEntityName());
        if(entity.getEntityName().endsWith("s"))
            msg = entity.getEntityName() + "' shop";
        else
            msg = entity.getEntityName() + "'s shop";
        hologram.addText(Text.literal(msg));

        Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
        hologram.addItemStack(item.getDefaultStack(), false);

        msg = String.format("%d", IntegerArgumentType.getInteger(context, "quantity"));
        hologram.addText(Text.literal(msg));

        msg = String.format("%.2f", DoubleArgumentType.getDouble(context, "price"));
        hologram.addText(Text.literal(msg).formatted(Formatting.GREEN));

        /*
        hologram.addElement(new CubeHitboxHologramElement(2, new Vec3d(0, -0.2, 0)) {
            @Override
            public void onClick(AbstractHologram hologram, ServerPlayerEntity player, InteractionType type, @Nullable Hand hand, @Nullable Vec3d vec, int entityId) {
                super.onClick(hologram, player, type, hand, vec, entityId);
                hologram.setElement(1, new EntityHologramElement(getEntityType(type == InteractionType.ATTACK).create(playerEntity.world)));
            }
        });
        */

        hologram.show();
    }

    private static EntityType getEntityType(boolean previous) {
        if (previous) {
            pos--;
        } else {
            pos++;
        }
        EntityType type = Registry.ENTITY_TYPE.get(pos);

        if (type == null) {
            pos = 0;
            type = Registry.ENTITY_TYPE.get(pos);
        }

        System.out.println(type);

        return type;
    }
}
