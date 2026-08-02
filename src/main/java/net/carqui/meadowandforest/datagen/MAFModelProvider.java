package net.carqui.meadowandforest.datagen;

import net.carqui.meadowandforest.MAF;
import net.carqui.meadowandforest.block.MAFBlocks;
import net.carqui.meadowandforest.block.MAFTomatoCropBlock;
import net.carqui.meadowandforest.item.MAFItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

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

        Block tomatoCrop = MAFBlocks.TOMATO_CROP.get();
        blockModels.registerSimpleFlatItemModel(tomatoCrop.asItem());
        MultiVariant trellisTopVariant = BlockModelGenerators.plainVariant(
                blockModels.createSuffixedVariant(tomatoCrop, "_trellised_top", ModelTemplates.CROP, TextureMapping::crop));
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(tomatoCrop).with(
                        PropertyDispatch.initial(CropBlock.AGE, MAFTomatoCropBlock.TRELLISED, MAFTomatoCropBlock.HALF)
                                .generate((age, trellised, half) -> half == DoubleBlockHalf.UPPER
                                        ? trellisTopVariant
                                        : BlockModelGenerators.plainVariant(
                                                blockModels.createSuffixedVariant(
                                                        tomatoCrop,
                                                        (trellised ? "_trellised_stage" : "_stage") + age,
                                                        ModelTemplates.CROP,
                                                        TextureMapping::crop)))
                )
        );
    }

}
