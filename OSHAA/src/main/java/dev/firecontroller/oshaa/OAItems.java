package dev.firecontroller.oshaa;

import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class OAItems {

    /**
     * Deferred register for items.
     */
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OSHAA.MOD_ID);

    public static final Supplier<BlockItem> EXIT_SIGN = ITEMS.registerSimpleBlockItem("exit_sign", OABlocks.EXIT_SIGN);

    private OAItems() {
        // ...
    }

}
