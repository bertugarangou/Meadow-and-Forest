package net.carqui.meadowandforest.block;

import net.carqui.meadowandforest.item.MAFItems;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;

public class MAFTomatoCropBlock extends CropBlock {

    public MAFTomatoCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return MAFItems.TOMATO_SEEDS.get();
    }
}
