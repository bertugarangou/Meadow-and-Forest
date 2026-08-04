package net.carqui.meadowandforest.datagen;

import net.carqui.meadowandforest.MAF;
import net.carqui.meadowandforest.block.MAFBlocks;
import net.carqui.meadowandforest.block.MAFGlassJarBlock;
import net.carqui.meadowandforest.block.MAFGlassJarVariants;
import net.carqui.meadowandforest.block.MAFTomatoCropBlock;
import net.carqui.meadowandforest.item.MAFDataComponents;
import net.carqui.meadowandforest.item.MAFItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class MAFModelProvider extends ModelProvider {
    public MAFModelProvider(PackOutput output) {
        super(output, MAF.MOD_ID);
    }

    // CandleBlock.CANDLES value -> word-based suffix used by the hand-authored
    // Blockbench models (glass_jar_one_jar.json, glass_jar_two_jars.json, ...)
    private static final Map<Integer, String> JAR_COUNT_SUFFIX = Map.of(
            1, "_one_jar",
            2, "_two_jars",
            3, "_three_jars",
            4, "_four_jars"
    );

    @Override
    protected void registerModels(BlockModelGenerators blockModels, @NonNull ItemModelGenerators itemModels) {

        //items
        MAFItems.ITEMS.getEntries().forEach(item -> {
            if (!(item.get() instanceof BlockItem)) {
                itemModels.generateFlatItem(item.get(), ModelTemplates.FLAT_ITEM);
            }
        });

        //blocks
        MAFBlocks.BLOCKS.getEntries().forEach(block -> {
            if (block.get() != MAFBlocks.TOMATO_CROP.get() && !(block.get() instanceof MAFGlassJarBlock)) {
                blockModels.createTrivialCube(block.get());
            }
        });

        MAFGlassJarVariants.ALL.forEach(variant -> {
            Block jarBlock = MAFBlocks.GLASS_JARS.get(variant.registryName()).get();

            // Item icon (every color): switch between the plain texture and
            // the "_firefly" texture based on FIREFLY_INSIDE_MARKER, which the
            // interaction handler sets/removes in lockstep with the real
            // FIREFLY_INSIDE block-state value stored on the stack. Every
            // color already has both a "<name>.png" and "<name>_firefly.png"
            // item texture on disk.
            ItemModel.Unbaked normalIcon = ItemModelUtils.plainModel(
                    itemModels.createFlatItemModel(jarBlock.asItem(), ModelTemplates.FLAT_ITEM));
            ItemModel.Unbaked fireflyIcon = ItemModelUtils.plainModel(
                    itemModels.createFlatItemModel(jarBlock.asItem(), "_firefly", ModelTemplates.FLAT_ITEM));

            itemModels.generateBooleanDispatch(
                    jarBlock.asItem(),
                    ItemModelUtils.hasComponent(MAFDataComponents.FIREFLY_INSIDE_MARKER.get()),
                    fireflyIcon,
                    normalIcon
            );

            if ("glass_jar".equals(variant.registryName())) {
                // Only the default glass variant has per-jar-count / firefly
                // block models authored right now (glass_jar_one_jar.json,
                // glass_jar_two_jars.json, ..., and the _firefly counterparts).
                blockModels.blockStateOutput.accept(
                        MultiVariantGenerator.dispatch(jarBlock).with(
                                PropertyDispatch.initial(CandleBlock.CANDLES, MAFGlassJarBlock.FIREFLY_INSIDE)
                                        .generate((count, firefly) -> BlockModelGenerators.plainVariant(
                                                Identifier.fromNamespaceAndPath(MAF.MOD_ID,
                                                        "block/glass_jar" + JAR_COUNT_SUFFIX.get(count) + (firefly ? "_firefly" : ""))
                                        ))
                        )
                );
            } else {
                // No per-count/firefly block models yet for the other 16
                // colors - keep the old single-model fallback so they don't break.
                blockModels.createNonTemplateModelBlock(jarBlock);
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