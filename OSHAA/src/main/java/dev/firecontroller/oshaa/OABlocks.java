package dev.firecontroller.oshaa;

import dev.firecontroller.oshaa.blocks.ExitSignBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class OABlocks {

    /**
     * Deferred register for blocks.
     */
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(OSHAA.MOD_ID);

    public static final Supplier<Block> EXIT_SIGN = BLOCKS.registerBlock("exit_sign", ExitSignBlock::new);

    private OABlocks() {
        // ...
    }

}
