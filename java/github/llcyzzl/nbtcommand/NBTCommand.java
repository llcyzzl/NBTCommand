package github.llcyzzl.nbtcommand;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * @author llcyzzl
 * @version 0.1.0-alpha
 */

public class NBTCommand implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("NBTCommand");
    @Override
    public void onInitialize() {
        LOGGER.info("NBTCommand is initializing!");
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> registerNBTCommand(dispatcher)));
    }

    private void registerNBTCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("nbt").requires(c -> c.hasPermissionLevel(2)).executes(context -> getNbtMainHand(context.getSource().getPlayer())));
    }

    private static int getNbtMainHand(ServerPlayerEntity player) {
        ItemStack itemStack = player.getMainHandStack();
        if(itemStack.hasNbt()){
            String nbt = itemStack.getNbt().toString();
            player.sendMessage(Text.empty());
            player.sendMessage(Text.literal(nbt)
                        .styled(style -> style
                            .withColor(Formatting.GRAY)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, nbt))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("commands.nbtcommand.message.copy")))), false);
            return Command.SINGLE_SUCCESS;
        } else {
            LOGGER.warn("[NbtCommand] ItemStack in player's main hand does not contain NBT data");
            player.sendMessage(Text.translatable("commands.nbtcommand.message.fail").formatted(Formatting.RED), false);
            return 0;
        }
    }
}
