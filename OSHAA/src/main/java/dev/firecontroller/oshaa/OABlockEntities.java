package dev.firecontroller.oshaa;

import dev.firecontroller.oshaa.block.entity.ExitSignBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class OABlockEntities {

    /**
     * Deferred register for block entities.
     */
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, OSHAA.MOD_ID);

    public static final Supplier<BlockEntityType<ExitSignBlockEntity>> EXIT_SIGN = BLOCK_ENTITIES.register("exit_sign", () -> BlockEntityType.Builder.of(
        ExitSignBlockEntity::new,
        OABlocks.LIGHT_EXIT_SIGN.get(),
        OABlocks.DARK_EXIT_SIGN.get()
    ).build(null));

    private OABlockEntities() {
        // ...
    }

}
