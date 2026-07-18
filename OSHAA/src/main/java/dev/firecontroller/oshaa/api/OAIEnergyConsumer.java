package dev.firecontroller.oshaa.api;

/**
 * Represents a component which can consume energy.
 */
public interface OAIEnergyConsumer {

    /**
     * Gets the configured energy profile for this energy consumer.
     * @return The consumer's energy profile.
     */
    OAEnergyProfile getEnergyProfile();

}
