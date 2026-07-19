package com.carqui.moddev.datagen;

import com.carqui.moddev.ModDev;
import com.carqui.moddev.item.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, ModDev.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
         itemModels.generateFlatItem(ModItems.TOMATO_SEEDS.get(), ModelTemplates.FLAT_ITEM);
         itemModels.generateFlatItem(ModItems.TOMATO.get(), ModelTemplates.FLAT_ITEM);
    }
}
