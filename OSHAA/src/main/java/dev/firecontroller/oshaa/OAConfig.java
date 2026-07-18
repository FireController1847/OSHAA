package dev.firecontroller.oshaa;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public final class OAConfig {

    public static final ModConfigSpec SERVER_SPEC = OAConfig.buildServer();

    // General
    public static ConfigValue<Boolean> enableEnergy;

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

        // Build the spec
        return builder.build();
    }

}
