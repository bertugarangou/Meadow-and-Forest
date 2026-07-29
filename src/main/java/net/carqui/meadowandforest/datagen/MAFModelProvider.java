package net.carqui.meadowandforest.datagen;

import net.carqui.meadowandforest.MAF;
import net.carqui.meadowandforest.block.MAFBlocks;
import net.carqui.meadowandforest.item.MAFItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;

public class MAFModelProvider extends ModelProvider {
    public MAFModelProvider(PackOutput output) {
        super(output, MAF.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
         //ITEMS - Generate models for all non-block items dynamically
         MAFItems.ITEMS.getEntries().forEach(item -> {
             if (!(item.get() instanceof BlockItem)) {
                 itemModels.generateFlatItem(item.get(), ModelTemplates.FLAT_ITEM);
             }
         });

        //BLOCKS - Generate models for all blocks dynamically
        MAFBlocks.BLOCKS.getEntries().forEach(block ->
            blockModels.createTrivialCube(block.get())
        );
    }
}
