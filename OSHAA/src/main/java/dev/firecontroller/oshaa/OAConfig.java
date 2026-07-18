package dev.firecontroller.oshaa;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public final class OAConfig {

    public static final ModConfigSpec SERVER_SPEC = OAConfig.buildServer();

    // Bulbs
    public static ConfigValue<Double> bulbsCarbonArcConsumption;
    public static ConfigValue<Double> bulbsIncandescentConsumption;
    public static ConfigValue<Double> bulbsHalogenConsumption;
    public static ConfigValue<Double> bulbsXenonArcConsumption;
    public static ConfigValue<Double> bulbsMercuryVaporConsumption;
    public static ConfigValue<Double> bulbsFluorescentConsumption;
    public static ConfigValue<Double> bulbsInductionConsumption;
    public static ConfigValue<Double> bulbsMetalHalideConsumption;
    public static ConfigValue<Double> bulbsHighPressureSodiumConsumption;
    public static ConfigValue<Double> bulbsLedConsumption;

    private static ModConfigSpec buildServer() {
        // Create the builder
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        // @formatter:off
        builder.translation("oshaa.configuration.items");
        builder.push("Items");

        builder.translation("oshaa.configuration.items.bulbs");
        builder.push("Bulbs");

        builder.translation("oshaa.configuration.bulbs.carbon_arc");
        builder.push("Carbon Arc");

        builder.worldRestart();
        builder.translation("oshaa.configuration.bulbs.consumption");
        builder.comment("The amount of FE/t consumed to energize this light bulb.");
        bulbsCarbonArcConsumption = builder.define("Consumption", 6.4);

        builder.pop();

        builder.translation("oshaa.configuration.bulbs.incandescent");
        builder.push("Incandescent");

        builder.worldRestart();
        builder.translation("oshaa.configuration.bulbs.consumption");
        builder.comment("The amount of FE/t consumed to energize this light bulb.");
        bulbsIncandescentConsumption = builder.define("Consumption", 2.14);

        builder.pop();

        builder.translation("oshaa.configuration.bulbs.halogen");
        builder.push("Halogen");

        builder.worldRestart();
        builder.translation("oshaa.configuration.bulbs.consumption");
        builder.comment("The amount of FE/t consumed to energize this light bulb.");
        bulbsHalogenConsumption = builder.define("Consumption", 1.46);

        builder.pop();

        builder.translation("oshaa.configuration.bulbs.xenon_arc");
        builder.push("Xenon Arc");

        builder.worldRestart();
        builder.translation("oshaa.configuration.bulbs.consumption");
        builder.comment("The amount of FE/t consumed to energize this light bulb.");
        bulbsXenonArcConsumption = builder.define("Consumption", 1.06);

        builder.pop();

        builder.translation("oshaa.configuration.bulbs.mercury_vapor");
        builder.push("Mercury Vapor");

        builder.worldRestart();
        builder.translation("oshaa.configuration.bulbs.consumption");
        builder.comment("The amount of FE/t consumed to energize this light bulb.");
        bulbsMercuryVaporConsumption = builder.define("Consumption", 0.72);

        builder.pop();

        builder.translation("oshaa.configuration.bulbs.fluorescent");
        builder.push("Fluorescent");

        builder.worldRestart();
        builder.translation("oshaa.configuration.bulbs.consumption");
        builder.comment("The amount of FE/t consumed to energize this light bulb.");
        bulbsFluorescentConsumption = builder.define("Consumption", 0.44);

        builder.pop();

        builder.translation("oshaa.configuration.bulbs.induction");
        builder.push("Induction");

        builder.worldRestart();
        builder.translation("oshaa.configuration.bulbs.consumption");
        builder.comment("The amount of FE/t consumed to energize this light bulb.");
        bulbsInductionConsumption = builder.define("Consumption", 0.38);

        builder.pop();

        builder.translation("oshaa.configuration.bulbs.metal_halide");
        builder.push("Metal Halide");

        builder.worldRestart();
        builder.translation("oshaa.configuration.bulbs.consumption");
        builder.comment("The amount of FE/t consumed to energize this light bulb.");
        bulbsMetalHalideConsumption = builder.define("Consumption", 0.36);

        builder.pop();

        builder.translation("oshaa.configuration.bulbs.high_pressure_sodium");
        builder.push("High-Pressure Sodium");

        builder.worldRestart();
        builder.translation("oshaa.configuration.bulbs.consumption");
        builder.comment("The amount of FE/t consumed to energize this light bulb.");
        bulbsHighPressureSodiumConsumption = builder.define("Consumption", 0.30);

        builder.pop();

        builder.translation("oshaa.configuration.bulbs.led");
        builder.push("LED");

        builder.worldRestart();
        builder.translation("oshaa.configuration.bulbs.consumption");
        builder.comment("The amount of FE/t consumed to energize this light bulb.");
        bulbsLedConsumption = builder.define("Consumption", 0.26);

        builder.pop();

        builder.pop();

        builder.pop();
        // @formatter:on

        // Build the spec
        return builder.build();
    }

}
