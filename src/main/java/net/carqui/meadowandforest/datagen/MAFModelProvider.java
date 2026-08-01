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
import net.minecraft.world.level.block.CropBlock;

public class MAFModelProvider extends ModelProvider {
    public MAFModelProvider(PackOutput output) {
        super(output, MAF.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {

        //items
        MAFItems.ITEMS.getEntries().forEach(item -> {
            if (!(item.get() instanceof BlockItem)) {
                itemModels.generateFlatItem(item.get(), ModelTemplates.FLAT_ITEM);
            }
        });

        //blocks
        MAFBlocks.BLOCKS.getEntries().forEach(block -> {
            if (block.get() != MAFBlocks.TOMATO_CROP.get()) {
                blockModels.createTrivialCube(block.get());
            }
        });

        blockModels.createCropBlock(MAFBlocks.TOMATO_CROP.get(), CropBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7);
    }

}
