package dev.firecontroller.oshaa;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
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
        OABlockEntities.BLOCK_ENTITIES.register(bus);
        OACreativeTabs.CREATIVE_TABS.register(bus);

        container.registerConfig(ModConfig.Type.COMMON, OAConfig.COMMON_SPEC);
    }

    /**
     * Event handler for the {@link FMLCommonSetupEvent}.
     */
    @SubscribeEvent
    private void onCommonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("This installation is OSHA Approved ✓");
    }

    @SubscribeEvent
    private void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        // EXIT_SIGN
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, OABlockEntities.EXIT_SIGN.get(), (blockEntity, side) -> blockEntity.getEnergyStorage());
    }

}
