package net.carqui.meadowandforest.datagen;

import net.carqui.meadowandforest.MAF;
import net.carqui.meadowandforest.block.MAFBlocks;
import net.carqui.meadowandforest.item.MAFItems;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MAFLanguageProvider extends LanguageProvider {
    public MAFLanguageProvider(PackOutput output, String locale) {
        super(output, MAF.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {

        // Automatically generate all items
        MAFItems.ITEMS.getEntries().forEach(item -> {
            if (!(item.get() instanceof BlockItem)) {
                add(item.getId().toLanguageKey("item"), makeName(item.getId().getPath()));
            }
        });


        // Automatically generate all blocks
        MAFBlocks.BLOCKS.getEntries().forEach(block -> {
            add(block.getId().toLanguageKey("block"), makeName(block.getId().getPath()));
        });

        add("creativetab.meadowandforest.tab_items", tabName());
    }


    private String makeName(String id) {
        return toTitleCase(id);
    }

    private String tabName() {
        return "Meadow And Forest";
    }

    private String toTitleCase(String id) {
        return Arrays.stream(id.split("_"))
                .filter(word -> !word.isEmpty())
                .map(word ->
                        word.substring(0, 1).toUpperCase()
                                + word.substring(1)
                )
                .collect(Collectors.joining(" "));
    }
}