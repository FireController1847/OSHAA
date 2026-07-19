package dev.firecontroller.oshaa;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OACreativeTabs {

    /**
     * Deferred register for creative tabs.
     */
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "oshaa");

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = CREATIVE_TABS.register("main", () ->
        CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.oshaa.main"))
                .icon(() -> new ItemStack(OAItems.INCANDESCENT_BULB.get()))
                .displayItems((parameters, output) -> {
                    output.accept(OAItems.SAFETY_BINDER.get());
                    output.accept(OAItems.ELECTRICIANS_GLOVE.get());

                    output.accept(OAItems.CARBON_ARC_BULB.get());
                    output.accept(OAItems.INCANDESCENT_BULB.get());
                    output.accept(OAItems.HALOGEN_BULB.get());
                    output.accept(OAItems.XENON_ARC_BULB.get());
                    output.accept(OAItems.MERCURY_VAPOR_BULB.get());
                    output.accept(OAItems.FLUORESCENT_BULB.get());
                    output.accept(OAItems.INDUCTION_BULB.get());
                    output.accept(OAItems.METAL_HALIDE_BULB.get());
                    output.accept(OAItems.HIGH_PRESSURE_SODIUM_BULB.get());
                    output.accept(OAItems.LED_BULB.get());

                    output.accept(OAItems.LIGHT_EXIT_SIGN.get());
                    output.accept(OAItems.DARK_EXIT_SIGN.get());
                })
            .build()
    );

    private OACreativeTabs() {
        // ...
    }

}
