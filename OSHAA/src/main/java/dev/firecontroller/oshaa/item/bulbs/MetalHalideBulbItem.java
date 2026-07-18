package dev.firecontroller.oshaa.item.bulbs;

import dev.firecontroller.oshaa.OAConfig;
import dev.firecontroller.oshaa.api.OAEnergyProfile;
import dev.firecontroller.oshaa.api.OAIEnergyConsumer;
import net.minecraft.world.item.Item;

public class MetalHalideBulbItem extends Item implements OAIEnergyConsumer {
    protected OAEnergyProfile energyProfile;

    /**
     * Constructs a new {@link MetalHalideBulbItem}.
     * @param properties Item properties to be assigned during construction.
     */
    public MetalHalideBulbItem(Properties properties) {
        super(properties);
        energyProfile = new OAEnergyProfile(OAConfig.bulbsMetalHalideConsumption.get());
    }

    @Override
    public OAEnergyProfile getEnergyProfile() {
        return energyProfile;
    }

}
