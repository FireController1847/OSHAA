package dev.firecontroller.oshaa.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.NotNull;

public class SafetyBinderItem extends Item {

    /**
     * Constructs a new {@link SafetyBinderItem}.
     * @param properties Item properties to be assigned during construction.
     */
    public SafetyBinderItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean doesSneakBypassUse(@NotNull ItemStack stack, @NotNull LevelReader level, @NotNull BlockPos pos, @NotNull Player player) {
        return true;
    }

}
