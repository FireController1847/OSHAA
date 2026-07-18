package dev.firecontroller.oshaa.item.bulbs;

import dev.firecontroller.oshaa.OAConfig;
import dev.firecontroller.oshaa.api.OAEnergyProfile;
import dev.firecontroller.oshaa.api.OAIEnergyConsumer;
import net.minecraft.world.item.Item;

public class FluorescentBulbItem extends Item implements OAIEnergyConsumer {
    protected OAEnergyProfile energyProfile;

    /**
     * Constructs a new {@link FluorescentBulbItem}.
     * @param properties Item properties to be assigned during construction.
     */
    public FluorescentBulbItem(Properties properties) {
        super(properties);
        energyProfile = new OAEnergyProfile(OAConfig.bulbsFluorescentConsumption.get());
    }

    @Override
    public OAEnergyProfile getEnergyProfile() {
        return energyProfile;
    }

}
