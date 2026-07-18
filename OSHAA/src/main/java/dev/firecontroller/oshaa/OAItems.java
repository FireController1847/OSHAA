package dev.firecontroller.oshaa;

import dev.firecontroller.oshaa.item.ElectriciansGloveItem;
import dev.firecontroller.oshaa.item.SafetyBinderItem;
import dev.firecontroller.oshaa.item.bulbs.CarbonArcBulbItem;
import dev.firecontroller.oshaa.item.bulbs.FluorescentBulbItem;
import dev.firecontroller.oshaa.item.bulbs.HalogenBulbItem;
import dev.firecontroller.oshaa.item.bulbs.HighPressureSodiumBulbItem;
import dev.firecontroller.oshaa.item.bulbs.IncandescentBulbItem;
import dev.firecontroller.oshaa.item.bulbs.InductionBulbItem;
import dev.firecontroller.oshaa.item.bulbs.LedBulbItem;
import dev.firecontroller.oshaa.item.bulbs.MercuryVaporBulbItem;
import dev.firecontroller.oshaa.item.bulbs.MetalHalideBulbItem;
import dev.firecontroller.oshaa.item.bulbs.XenonArcBulbItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class OAItems {

    /**
     * Deferred register for items.
     */
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OSHAA.MOD_ID);

    public static final Supplier<Item> SAFETY_BINDER = ITEMS.registerItem("safety_binder", SafetyBinderItem::new);
    public static final Supplier<Item> ELECTRICIANS_GLOVE = ITEMS.registerItem("electricians_glove", ElectriciansGloveItem::new);

    public static final Supplier<Item> CARBON_ARC_BULB = ITEMS.registerItem("carbon_arc_bulb", CarbonArcBulbItem::new);
    public static final Supplier<Item> INCANDESCENT_BULB = ITEMS.registerItem("incandescent_bulb", IncandescentBulbItem::new);
    public static final Supplier<Item> HALOGEN_BULB = ITEMS.registerItem("halogen_bulb", HalogenBulbItem::new);
    public static final Supplier<Item> XENON_ARC_BULB = ITEMS.registerItem("xenon_arc_bulb", XenonArcBulbItem::new);
    public static final Supplier<Item> MERCURY_VAPOR_BULB = ITEMS.registerItem("mercury_vapor_bulb", MercuryVaporBulbItem::new);
    public static final Supplier<Item> FLUORESCENT_BULB = ITEMS.registerItem("fluorescent_bulb", FluorescentBulbItem::new);
    public static final Supplier<Item> INDUCTION_BULB = ITEMS.registerItem("induction_bulb", InductionBulbItem::new);
    public static final Supplier<Item> METAL_HALIDE_BULB = ITEMS.registerItem("metal_halide_bulb", MetalHalideBulbItem::new);
    public static final Supplier<Item> HIGH_PRESSURE_SODIUM_BULB = ITEMS.registerItem("high_pressure_sodium_bulb", HighPressureSodiumBulbItem::new);
    public static final Supplier<Item> LED_BULB = ITEMS.registerItem("led_bulb", LedBulbItem::new);

    public static final Supplier<BlockItem> EXIT_SIGN = ITEMS.registerSimpleBlockItem("exit_sign", OABlocks.EXIT_SIGN);

    private OAItems() {
        // ...
    }

}
