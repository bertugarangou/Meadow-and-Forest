package net.carqui.meadowandforest.datagen;

import net.carqui.meadowandforest.MAF;
import net.carqui.meadowandforest.block.MAFBlocks;
import net.carqui.meadowandforest.item.MAFItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;

public class MAFModelProvider extends ModelProvider {
    public MAFModelProvider(PackOutput output) {
        super(output, MAF.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
         //ITEMS
         itemModels.generateFlatItem(MAFItems.TOMATO_SEEDS.get(), ModelTemplates.FLAT_ITEM);
         itemModels.generateFlatItem(MAFItems.TOMATO.get(), ModelTemplates.FLAT_ITEM);

        //BLOCKS
        blockModels.createTrivialCube(MAFBlocks.BLOCK_NAME.get());
    }
}
