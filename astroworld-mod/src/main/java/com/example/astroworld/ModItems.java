package com.example.astroworld;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

public final class ModItems {
    public static final Item CACTUS_JACK_MIC = new CactusJackMicItem(
            new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC));

    private ModItems() {
    }

    public static void register() {
        Registry.register(Registries.ITEM, AstroworldMod.id("cactus_jack_mic"), CACTUS_JACK_MIC);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(CACTUS_JACK_MIC));
    }
}
