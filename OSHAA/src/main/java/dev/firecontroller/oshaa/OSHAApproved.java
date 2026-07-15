package dev.firecontroller.oshaa;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(OSHAApproved.MOD_ID)
public final class OSHAApproved {

    public static final String MOD_ID = "oshaa";
    public static final Logger LOGGER = LogManager.getLogger(OSHAApproved.MOD_ID);

    /**
     * Constructs a new {@link OSHAApproved}.
     * @param bus An instance of the NeoForge EventBus API.
     * @param container An instance of the NeoForge ModContainer API.
     */
    public OSHAApproved(IEventBus bus, ModContainer container) {
        bus.register(this);
    }

    /**
     * Event handler for the {@link FMLCommonSetupEvent}.
     */
    @SubscribeEvent
    private void onCommonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("This installation is OSHA Approved ✓");
    }

}
