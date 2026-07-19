package dev.firecontroller.oshaa.item.bulbs;

import dev.firecontroller.oshaa.OAConfig;
import dev.firecontroller.oshaa.api.OAEnergyProfile;
import dev.firecontroller.oshaa.api.OAIEnergyConsumer;
import net.minecraft.world.item.Item;

public final class CarbonArcBulbItem extends Item implements OAIEnergyConsumer {

    /**
     * Constructs a new {@link CarbonArcBulbItem}.
     * @param properties Item properties to be assigned during construction.
     */
    public CarbonArcBulbItem(Properties properties) {
        super(properties);
    }

    @Override
    public OAEnergyProfile getEnergyProfile() {
        return new OAEnergyProfile(OAConfig.bulbsCarbonArcConsumption.get());
    }

}
