package dev.firecontroller.oshaa.item.bulbs;

import dev.firecontroller.oshaa.OAConfig;
import dev.firecontroller.oshaa.api.OAEnergyProfile;
import dev.firecontroller.oshaa.api.OAIEnergyConsumer;
import net.minecraft.world.item.Item;

public class IncandescentBulbItem extends Item implements OAIEnergyConsumer {

    /**
     * Constructs a new {@link IncandescentBulbItem}.
     * @param properties Item properties to be assigned during construction.
     */
    public IncandescentBulbItem(Properties properties) {
        super(properties);
    }

    @Override
    public OAEnergyProfile getEnergyProfile() {
        return new OAEnergyProfile(OAConfig.bulbsIncandescentConsumption.get());
    }

}
