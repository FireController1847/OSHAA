package dev.firecontroller.oshaa.api;

/**
 * Represents the energy consumption parameters of an energy device.
 *
 * @param continuous The amount of FE/t consumed during device operation.
 * @param standby The amount of FE/t consumed during the device standby.
 * @param startup The amount of FE consumed when the device begins operating.
 */
public record OAEnergyProfile(double continuous, double standby, int startup) {

    /**
     * Constructs a new {@link OAEnergyProfile} with
     * the specified continuous amount and
     * zero standby or startup costs.
     * @param continuous The amount of FE/t consumed during device operation.
     */
    public OAEnergyProfile(double continuous) {
        this(continuous, 0, 0);
    }

}
