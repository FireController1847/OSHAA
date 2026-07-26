package dev.firecontroller.oshaa.item.bulbs;

import dev.firecontroller.oshaa.OAConfig;
import dev.firecontroller.oshaa.api.OAEnergyProfile;
import dev.firecontroller.oshaa.api.OAIEnergyConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class CarbonArcBulbItem extends Item implements OAIEnergyConsumer {

    /**
     * Constructs a new {@link CarbonArcBulbItem}.
     * @param properties Item properties to be assigned during construction.
     */
    public CarbonArcBulbItem(Properties properties) {
        super(properties);
    }

    @Override
    public OAEnergyProfile getEnergyProfile() {
        return new OAEnergyProfile(OAConfig.bulbsCarbonArcConsumption.get());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("tooltip.oshaa.item.carbon_arc_bulb").withStyle(ChatFormatting.BLUE).withStyle(ChatFormatting.ITALIC));
        tooltipComponents.add(
            Component.translatable(
                "tooltip.oshaa.energy_usage",
                Component.literal(OAConfig.bulbsCarbonArcConsumption.get() + "FE/t").withStyle(ChatFormatting.DARK_GRAY)
            ).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC)
        );
    }

}
