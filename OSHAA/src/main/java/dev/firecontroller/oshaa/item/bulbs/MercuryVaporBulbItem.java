package dev.firecontroller.oshaa.item.bulbs;

import dev.firecontroller.oshaa.OAConfig;
import dev.firecontroller.oshaa.api.OAEnergyProfile;
import dev.firecontroller.oshaa.api.OAIEnergyConsumer;
import net.minecraft.world.item.Item;

public class MercuryVaporBulbItem extends Item implements OAIEnergyConsumer {
    protected OAEnergyProfile energyProfile;

    /**
     * Constructs a new {@link MercuryVaporBulbItem}.
     * @param properties Item properties to be assigned during construction.
     */
    public MercuryVaporBulbItem(Properties properties) {
        super(properties);
        energyProfile = new OAEnergyProfile(OAConfig.bulbsMercuryVaporConsumption.get());
    }

    @Override
    public OAEnergyProfile getEnergyProfile() {
        return energyProfile;
    }

}
