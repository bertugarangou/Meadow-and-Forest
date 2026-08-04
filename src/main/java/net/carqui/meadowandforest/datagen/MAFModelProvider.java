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
            //exceptions manually handled below
            if (block.get() != MAFBlocks.TOMATO_CROP.get() && !(block.get() instanceof MAFGlassJarBlock)) {
                blockModels.createTrivialCube(block.get());
            }
        });

        //glass jars
        MAFGlassJarVariants.ALL.forEach(variant -> {
            Block jarBlock = MAFBlocks.GLASS_JARS.get(variant.registryName()).get();

            //item on inventory format:
            //<color>_glass_jar_one_jar.json
            //<color>_glass_jar_two_jars.json
            //glass_jar_..._jars.json
            ItemModel.Unbaked normalIcon = ItemModelUtils.plainModel(
                    itemModels.createFlatItemModel(jarBlock.asItem(), ModelTemplates.FLAT_ITEM)); //no firefly
            ItemModel.Unbaked fireflyIcon = ItemModelUtils.plainModel(
                    itemModels.createFlatItemModel(jarBlock.asItem(), "_firefly", ModelTemplates.FLAT_ITEM)); //with firefly


            itemModels.generateBooleanDispatch(
                    jarBlock.asItem(),
                    ItemModelUtils.hasComponent(MAFDataComponents.FIREFLY_INSIDE_MARKER.get()),
                    fireflyIcon,
                    normalIcon
            );

            final String baseName = variant.registryName();

            blockModels.blockStateOutput.accept(
                    MultiVariantGenerator.dispatch(jarBlock).with(
                            PropertyDispatch.initial(CandleBlock.CANDLES, MAFGlassJarBlock.FIREFLY_INSIDE)
                                    .generate((count, firefly) -> BlockModelGenerators.plainVariant(
                                            Identifier.fromNamespaceAndPath(
                                                    MAF.MOD_ID,
                                                    "block/"
                                                            + baseName
                                                            + JAR_COUNT_SUFFIX.get(count)
                                                            + (firefly ? "_firefly" : "")
                                            )
                                    ))
                    )
            );
        });

        //tomato crop
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