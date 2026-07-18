package dev.firecontroller.oshaa.api;

/**
 *
 * @param interval How often the consumer performs withdrawal in ticks.
 * @param draw The amount to withdraw per interval.
 */
public record OAEnergyDraw(int interval, int draw) {
    // ...
}
