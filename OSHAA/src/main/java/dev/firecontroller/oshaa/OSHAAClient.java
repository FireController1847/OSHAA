package dev.firecontroller.oshaa;

import dev.firecontroller.oshaa.block.entity.ExitSignBlockEntity;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = OSHAA.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = OSHAA.MOD_ID, value = Dist.CLIENT)
public final class OSHAAClient {

    /**
     * Constructs a new {@link OSHAAClient}.
     * @param container An instance of the NeoForge ModContainer API.
     */
    public OSHAAClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    /**
     * Event handler for the {@link RegisterColorHandlersEvent.Block}.
     */
    @SubscribeEvent
    public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        // EXIT_SIGN
        event.register((state, level, pos, tintIndex) -> {
            if (tintIndex != 0) return 0xFFFFFFFF;
            if (level != null && pos != null && level.getBlockEntity(pos) instanceof ExitSignBlockEntity exitSign) {
                return 0xFF000000 | exitSign.getColor();
            }
            return 0xFF000000 | ExitSignBlockEntity.DEFAULT_COLOR;
        }, OABlocks.EXIT_SIGN.get());
    }

    /**
     * Event handler for the {@link RegisterColorHandlersEvent.Item}.
     */
    @SubscribeEvent
    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        // EXIT_SIGN
        event.register((stack, tintIndex) -> {
            if (tintIndex != 0) return 0xFFFFFFFF;
            int color = DyedItemColor.getOrDefault(stack, ExitSignBlockEntity.DEFAULT_COLOR);
            return 0xFF000000 | (color & 0x00FFFFFF);
        }, OAItems.EXIT_SIGN.get());
    }

}
