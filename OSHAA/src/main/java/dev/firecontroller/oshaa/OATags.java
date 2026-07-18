package dev.firecontroller.oshaa;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class OATags {

    public static final class Items {

        public static final TagKey<Item> LIGHT_BULB = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("oshaa", "light_bulb"));

        private Items() {
            // ...
        }

    }

    private OATags() {
        // ...
    }

}
