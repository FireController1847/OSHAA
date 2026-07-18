package dev.firecontroller.oshaa.item.bulbs;

import dev.firecontroller.oshaa.OAConfig;
import dev.firecontroller.oshaa.api.OAEnergyProfile;
import dev.firecontroller.oshaa.api.OAIEnergyConsumer;
import net.minecraft.world.item.Item;

public class HalogenBulbItem extends Item implements OAIEnergyConsumer {
    protected OAEnergyProfile energyProfile;

    /**
     * Constructs a new {@link HalogenBulbItem}.
     * @param properties Item properties to be assigned during construction.
     */
    public HalogenBulbItem(Properties properties) {
        super(properties);
        energyProfile = new OAEnergyProfile(OAConfig.bulbsHalogenConsumption.get());
    }

    @Override
    public OAEnergyProfile getEnergyProfile() {
        return energyProfile;
    }

}
