package dev.firecontroller.oshaa.item.bulbs;

import dev.firecontroller.oshaa.OAConfig;
import dev.firecontroller.oshaa.api.OAEnergyProfile;
import dev.firecontroller.oshaa.api.OAIEnergyConsumer;
import net.minecraft.world.item.Item;

public final class LedBulbItem extends Item implements OAIEnergyConsumer {

    /**
     * Constructs a new {@link LedBulbItem}.
     * @param properties Item properties to be assigned during construction.
     */
    public LedBulbItem(Properties properties) {
        super(properties);
    }

    @Override
    public OAEnergyProfile getEnergyProfile() {
        return new OAEnergyProfile(OAConfig.bulbsLedConsumption.get());
    }

}
