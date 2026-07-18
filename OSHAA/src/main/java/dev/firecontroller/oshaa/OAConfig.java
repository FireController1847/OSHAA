package dev.firecontroller.oshaa;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public final class OAConfig {

    public static final ModConfigSpec SERVER_SPEC = OAConfig.buildServer();

    // General
    public static ConfigValue<Boolean> enableEnergy;

    // Bulbs
    public static ConfigValue<Double> bulbsCarbonArcConsumption;
    public static ConfigValue<Integer> bulbsCarbonArcCapacity;
    public static ConfigValue<Double> bulbsIncandescentConsumption;
    public static ConfigValue<Integer> bulbsIncandescentCapacity;
    public static ConfigValue<Double> bulbsHalogenConsumption;
    public static ConfigValue<Integer> bulbsHalogenCapacity;
    public static ConfigValue<Double> bulbsXenonArcConsumption;
    public static ConfigValue<Integer> bulbsXenonArcCapacity;
    public static ConfigValue<Double> bulbsMercuryVaporConsumption;
    public static ConfigValue<Integer> bulbsMercuryVaporCapacity;
    public static ConfigValue<Double> bulbsFluorescentConsumption;
    public static ConfigValue<Integer> bulbsFluorescentCapacity;
    public static ConfigValue<Double> bulbsInductionConsumption;
    public static ConfigValue<Integer> bulbsInductionCapacity;
    public static ConfigValue<Double> bulbsMetalHalideConsumption;
    public static ConfigValue<Integer> bulbsMetalHalideCapacity;
    public static ConfigValue<Double> bulbsHighPressureSodiumConsumption;
    public static ConfigValue<Integer> bulbsHighPressureSodiumCapacity;
    public static ConfigValue<Double> bulbsLedConsumption;
    public static ConfigValue<Integer> bulbsLedCapacity;

    private static ModConfigSpec buildServer() {
        // Create the builder
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        // @formatter:off
        builder.translation("oshaa.configuration.energy");
        builder.push("Energy");

        builder.worldRestart();
        builder.translation("oshaa.configuration.energy.enabled");
        builder.comment("When disabled, functional decorations will work without requiring energy.");
        enableEnergy = builder.define("Enabled", true);

        builder.pop();
        // @formatter:on

        // @formatter:off
        builder.translation("oshaa.configuration.items");
        builder.push("Items");

        builder.translation("oshaa.configuration.items.bulbs");
        builder.push("Bulbs");

        builder.translation("oshaa.configuration.bulbs.carbon_arc");
        builder.push("Carbon Arc");

        builder.translation("oshaa.configuration.bulbs.carbon_arc.consumption");
        builder.comment("The amount of FE/t consumed to energize this light bulb to produce 1,600 lumens.");
        bulbsCarbonArcConsumption = builder.define("Consumption", 6.4);

        builder.translation("oshaa.configuration.bulbs.carbon_arc.capacity");
        builder.comment("The amount of FE which must be stored in order to operate this light bulb for at least 60 seconds.");
        bulbsCarbonArcCapacity = builder.define("Capacity", 7680);

        builder.pop();

        builder.translation("oshaa.configuration.bulbs.incandescent");
        builder.push("Incandescent");

        builder.translation("oshaa.configuration.bulbs.incandescent.consumption");
        builder.comment("The amount of FE/t consumed to energize this light bulb to produce 1,600 lumens.");
        bulbsIncandescentConsumption = builder.define("Consumption", 2.14);

        builder.translation("oshaa.configuration.bulbs.incandescent.capacity");
        builder.comment("The amount of FE which must be stored in order to operate this light bulb for at least 60 seconds.");
        bulbsIncandescentCapacity = builder.define("Capacity", 2568);

        builder.pop();

        builder.translation("oshaa.configuration.bulbs.halogen");
        builder.push("Halogen");

        builder.translation("oshaa.configuration.bulbs.halogen.consumption");
        builder.comment("The amount of FE/t consumed to energize this light bulb to produce 1,600 lumens.");
        bulbsHalogenConsumption = builder.define("Consumption", 1.46);

        builder.translation("oshaa.configuration.bulbs.halogen.capacity");
        builder.comment("The amount of FE which must be stored in order to operate this light bulb for at least 60 seconds.");
        bulbsHalogenCapacity = builder.define("Capacity", 1752);

        builder.pop();

        builder.translation("oshaa.configuration.bulbs.xenon_arc");
        builder.push("Xenon Arc");

        builder.translation("oshaa.configuration.bulbs.xenon_arc.consumption");
        builder.comment("The amount of FE/t consumed to energize this light bulb to produce 1,600 lumens.");
        bulbsXenonArcConsumption = builder.define("Consumption", 1.06);

        builder.translation("oshaa.configuration.bulbs.xenon_arc.capacity");
        builder.comment("The amount of FE which must be stored in order to operate this light bulb for at least 60 seconds.");
        bulbsXenonArcCapacity = builder.define("Capacity", 1272);

        builder.pop();

        builder.translation("oshaa.configuration.bulbs.mercury_vapor");
        builder.push("Mercury Vapor");

        builder.translation("oshaa.configuration.bulbs.mercury_vapor.consumption");
        builder.comment("The amount of FE/t consumed to energize this light bulb to produce 1,600 lumens.");
        bulbsMercuryVaporConsumption = builder.define("Consumption", 0.72);

        builder.translation("oshaa.configuration.bulbs.mercury_vapor.capacity");
        builder.comment("The amount of FE which must be stored in order to operate this light bulb for at least 60 seconds.");
        bulbsMercuryVaporCapacity = builder.define("Capacity", 864);

        builder.pop();

        builder.translation("oshaa.configuration.bulbs.fluorescent");
        builder.push("Fluorescent");

        builder.translation("oshaa.configuration.bulbs.fluorescent.consumption");
        builder.comment("The amount of FE/t consumed to energize this light bulb to produce 1,600 lumens.");
        bulbsFluorescentConsumption = builder.define("Consumption", 0.44);

        builder.translation("oshaa.configuration.bulbs.fluorescent.capacity");
        builder.comment("The amount of FE which must be stored in order to operate this light bulb for at least 60 seconds.");
        bulbsFluorescentCapacity = builder.define("Capacity", 528);

        builder.pop();

        builder.translation("oshaa.configuration.bulbs.induction");
        builder.push("Induction");

        builder.translation("oshaa.configuration.bulbs.induction.consumption");
        builder.comment("The amount of FE/t consumed to energize this light bulb to produce 1,600 lumens.");
        bulbsInductionConsumption = builder.define("Consumption", 0.38);

        builder.translation("oshaa.configuration.bulbs.induction.capacity");
        builder.comment("The amount of FE which must be stored in order to operate this light bulb for at least 60 seconds.");
        bulbsInductionCapacity = builder.define("Capacity", 456);

        builder.pop();

        builder.translation("oshaa.configuration.bulbs.metal_halide");
        builder.push("Metal Halide");

        builder.translation("oshaa.configuration.bulbs.metal_halide.consumption");
        builder.comment("The amount of FE/t consumed to energize this light bulb to produce 1,600 lumens.");
        bulbsMetalHalideConsumption = builder.define("Consumption", 0.36);

        builder.translation("oshaa.configuration.bulbs.metal_halide.capacity");
        builder.comment("The amount of FE which must be stored in order to operate this light bulb for at least 60 seconds.");
        bulbsMetalHalideCapacity = builder.define("Capacity", 432);

        builder.pop();

        builder.translation("oshaa.configuration.bulbs.high_pressure_sodium");
        builder.push("High-Pressure Sodium");

        builder.translation("oshaa.configuration.bulbs.high_pressure_sodium.consumption");
        builder.comment("The amount of FE/t consumed to energize this light bulb to produce 1,600 lumens.");
        bulbsHighPressureSodiumConsumption = builder.define("Consumption", 0.30);

        builder.translation("oshaa.configuration.bulbs.high_pressure_sodium.capacity");
        builder.comment("The amount of FE which must be stored in order to operate this light bulb for at least 60 seconds.");
        bulbsHighPressureSodiumCapacity = builder.define("Capacity", 360);

        builder.pop();

        builder.translation("oshaa.configuration.bulbs.led");
        builder.push("LED");

        builder.translation("oshaa.configuration.bulbs.led.consumption");
        builder.comment("The amount of FE/t consumed to energize this light bulb to produce 1,600 lumens.");
        bulbsLedConsumption = builder.define("Consumption", 0.26);

        builder.translation("oshaa.configuration.bulbs.led.capacity");
        builder.comment("The amount of FE which must be stored in order to operate this light bulb for at least 60 seconds.");
        bulbsLedCapacity = builder.define("Capacity", 312);

        builder.pop();

        builder.pop();

        builder.pop();
        // @formatter:on

        // Build the spec
        return builder.build();
    }

}
