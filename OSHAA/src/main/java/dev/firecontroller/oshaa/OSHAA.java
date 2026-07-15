package dev.firecontroller.oshaa;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(OSHAA.MOD_ID)
public final class OSHAA {

    public static final String MOD_ID = "oshaa";
    public static final Logger LOGGER = LogManager.getLogger(OSHAA.MOD_ID);

    /**
     * Constructs a new {@link OSHAA}.
     * @param bus An instance of the NeoForge EventBus API.
     * @param container An instance of the NeoForge ModContainer API.
     */
    public OSHAA(IEventBus bus, ModContainer container) {
        bus.register(this);

        OABlocks.BLOCKS.register(bus);
        OAItems.ITEMS.register(bus);
    }

    /**
     * Event handler for the {@link FMLCommonSetupEvent}.
     */
    @SubscribeEvent
    private void onCommonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("This installation is OSHA Approved ✓");
    }

    /**
     * Event handler for the {@link BuildCreativeModeTabContentsEvent}.
     */
    @SubscribeEvent
    private void onRegisterCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        // TODO: Temporary! We need to register our own creative tab.
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(OAItems.EXIT_SIGN.get());
        }
    }

}
