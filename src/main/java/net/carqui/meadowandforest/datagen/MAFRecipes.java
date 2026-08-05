package net.carqui.meadowandforest.datagen;

import net.carqui.meadowandforest.MAF;
import net.carqui.meadowandforest.block.MAFBlocks;
import net.carqui.meadowandforest.block.MAFGlassJarVariants;
import net.carqui.meadowandforest.item.MAFItems;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.NeoForgeConditions;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

/**
 * Registers every crafting, smelting, smoking, and campfire recipe for the mod,
 * including glass jars, breads, tomato dishes, and the cane trellis item.
 */
public class MAFRecipes extends RecipeProvider {
    /**
     * Creates the recipe provider bound to this mod's registries and output.
     */
    protected MAFRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    /**
     * Datagen runner that wires {@link MAFRecipes} into the recipe provider pipeline.
     */
    public static class Runner extends RecipeProvider.Runner {
        /**
         * Creates the runner bound to this mod's pack output and registries.
         */
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries){
            super(packOutput, registries);
        }

        /**
         * Creates the actual {@link MAFRecipes} instance used to build recipes.
         */
        @Override
        protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.@NonNull Provider registries, @NonNull RecipeOutput output) {
            return new MAFRecipes(registries, output);
        }

        /**
         * Returns the display name shown for this datagen task.
         */
        @Override
        public @NonNull String getName() {
            return "Meadow and Forest Recipes";
        }
    }

    /**
     * Builds every recipe the mod defines: glass jars, seeds, breads, tomato
     * dishes, cooking variants, and the cane, plus removing vanilla bread.
     */
    @Override
    protected void buildRecipes() {
        glassJarRecipes();

        //tomato seeds from tomato
        shapeless(RecipeCategory.FOOD, MAFItems.TOMATO_SEEDS.get(), 3)
                .requires(MAFItems.TOMATO)
                .unlockedBy(getHasName(MAFItems.TOMATO_SEEDS.get()), has(MAFItems.TOMATO_SEEDS))
                .group("tomato_seeds")
                .save(output /*, "meadowandforest:tomato_seeds"*/);

        //bread dough from wheat
        shapeless(RecipeCategory.FOOD, MAFItems.BREADDOUGH.get(), 1)
                .requires(Items.WHEAT)
                .unlockedBy(getHasName(Items.WHEAT), has(Items.WHEAT))
                .group("breaddough")
                .save(output);

        //small, big and baguette bread dough
        shaped(RecipeCategory.FOOD, MAFItems.SMALL_BREAD_BREADDOUGH.get(),1)
                .pattern("BB ")
                .pattern("   ")
                .pattern("   ")
                .define('B', MAFItems.BREADDOUGH.get())
                .unlockedBy(getHasName(MAFItems.BREADDOUGH.get()), has(MAFItems.BREADDOUGH.get()))
                .group("small_breaddough")
                .save(output);
        shaped(RecipeCategory.FOOD, MAFItems.BIG_BREAD_BREADDOUGH.get(),1)
                .pattern("BB ")
                .pattern("BB ")
                .pattern("   ")
                .define('B', MAFItems.BREADDOUGH.get())
                .unlockedBy(getHasName(MAFItems.BREADDOUGH.get()), has(MAFItems.BREADDOUGH.get()))
                .group("big_breaddough")
                .save(output);
        shaped(RecipeCategory.FOOD, MAFItems.BAGUETTE_BREAD_BREADDOUGH.get(),1)
                .pattern("BBB")
                .pattern("   ")
                .pattern("   ")
                .define('B', MAFItems.BREADDOUGH.get())
                .unlockedBy(getHasName(MAFItems.BREADDOUGH.get()), has(MAFItems.BREADDOUGH.get()))
                .group("baguette_breaddough")
                .save(output);

        //delete minecraft default bread recipe
        shaped(RecipeCategory.FOOD, Items.BREAD)
                .pattern("WWW")
                .define('W', Items.WHEAT)
                .showNotification(false)
                .unlockedBy("never", has(Items.WHEAT))
                .save(output.withConditions(NeoForgeConditions.never()));

        //slice of bread
        shapeless(RecipeCategory.FOOD, MAFItems.SLICE_OF_BREAD.get(), 8)
                .requires(MAFItems.WHOLE_BREAD.get())
                .unlockedBy(getHasName(MAFItems.WHOLE_BREAD.get()), has(MAFItems.WHOLE_BREAD.get()))
                .group("slice_of_bread")
                .save(output, "slice_of_bread_from_whole_bread");
        shapeless(RecipeCategory.FOOD, MAFItems.SLICE_OF_BREAD.get(), 4)
                .requires(MAFItems.HALF_BREAD.get())
                .unlockedBy(getHasName(MAFItems.HALF_BREAD.get()), has(MAFItems.HALF_BREAD.get()))
                .group("slice_of_bread")
                .save(output, "slice_of_bread_from_half_bread");
        shapeless(RecipeCategory.FOOD, MAFItems.SLICE_OF_BREAD.get(), 6)
                .requires(Items.BREAD)
                .unlockedBy(getHasName(Items.BREAD), has(Items.BREAD))
                .group("slice_of_bread")
                .save(output, "slice_of_bread_from_minecraftbread");

        shapeless(RecipeCategory.FOOD, MAFItems.PA_AMB_TOMATA.get(), 1)
                .requires(MAFItems.SLICE_OF_BREAD.get())
                .requires(MAFItems.VINE_TOMATO.get())
                .unlockedBy(getHasName(MAFItems.SLICE_OF_BREAD.get()), has(MAFItems.SLICE_OF_BREAD.get()))
                .unlockedBy(getHasName(MAFItems.VINE_TOMATO.get()), has(MAFItems.VINE_TOMATO.get()))
                .group("pa_amb_tomata")
                .save(output, "pa_amb_tomata");

        shapeless(RecipeCategory.FOOD, MAFItems.XAVA_ABERRATION.get(), 1)
                .requires(MAFItems.SLICE_OF_BREAD.get())
                .requires(MAFItems.TOMATO.get())
                .unlockedBy(getHasName(MAFItems.SLICE_OF_BREAD.get()), has(MAFItems.SLICE_OF_BREAD.get()))
                .unlockedBy(getHasName(MAFItems.TOMATO_SOUP.get()), has(MAFItems.TOMATO_SOUP.get()))
                .group("aberracio_xava")
                .save(output, "aberracio_xava");

        shapeless(RecipeCategory.FOOD, MAFItems.TOMATO_SOUP.get(), 1)
                .requires(MAFItems.TOMATO.get(), 3)
                .requires(Items.BOWL, 1)
                .unlockedBy(getHasName(Items.BOWL), has(Items.BOWL))
                .unlockedBy(getHasName(MAFItems.TOMATO.get()), has(MAFItems.TOMATO.get()))
                .group("tomato_soup")
                .save(output, "tomato_soup_from_tomato");
        shapeless(RecipeCategory.FOOD, MAFItems.TOMATO_SOUP.get(), 1)
                .requires(MAFItems.VINE_TOMATO.get(), 6)
                .requires(Items.BOWL, 1)
                .unlockedBy(getHasName(Items.BOWL), has(Items.BOWL))
                .unlockedBy(getHasName(MAFItems.VINE_TOMATO.get()), has(MAFItems.VINE_TOMATO.get()))
                .group("tomato_soup")
                .save(output, "tomato_soup_from_vine_tomato");

        //breaddough cooking
        cookingRecipes(output, CookingKind.FOOD, "small_bread_breaddough", MAFItems.SMALL_BREAD_BREADDOUGH.get(), MAFItems.HALF_BREAD, 0.1f, 160, "small_bread", getHasName(MAFItems.BREADDOUGH.get()), has(MAFItems.BREADDOUGH.get()));
        cookingRecipes(output, CookingKind.FOOD, "big_bread_breaddough", MAFItems.BIG_BREAD_BREADDOUGH.get(), MAFItems.WHOLE_BREAD, 0.2f, 160, "big_bread", getHasName(MAFItems.BREADDOUGH.get()), has(MAFItems.BREADDOUGH.get()));
        cookingRecipes(output, CookingKind.FOOD, "baguette_bread_breaddough", MAFItems.BAGUETTE_BREAD_BREADDOUGH.get(), Items.BREAD, 0.15f, 160, "baguette", getHasName(MAFItems.BREADDOUGH.get()), has(MAFItems.BREADDOUGH.get()));

        //basil cooking
        cookingRecipes(output, CookingKind.FOOD, "basil", MAFItems.BASIL_LEAVES.get(), MAFItems.BASIL_LEAVES_DRIED.get(), 0.1f, 120, "dry_basil", getHasName(MAFItems.BASIL_LEAVES.get()), has(MAFItems.BASIL_LEAVES.get()));

        //cane recipe from sticks
        shaped(RecipeCategory.MISC, MAFItems.CANE.get())
                .pattern("I I")
                .pattern(" S ")
                .pattern("I I")
                .group("cane")
                .define('I', Items.STICK)
                .define('S', Items.STRING)
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .save(output);

    }

    /**
     * Registers the shaped crafting recipe for every glass jar color variant.
     */
    private void glassJarRecipes() {
        for (MAFGlassJarVariants.Variant variant : MAFGlassJarVariants.ALL) {
            shaped(RecipeCategory.MISC, MAFBlocks.GLASS_JARS.get(variant.registryName()).get())
                    .pattern("   ")
                    .pattern("G G")
                    .pattern("GGG")
                    .define('G', variant.ingredient())
                    .unlockedBy(getHasName(Blocks.GLASS), has(Blocks.GLASS))
                    .group("glass_jar")
                    .save(output);
        }
    }

    //cooking helper class and utils
    /**
     * Categorizes an ingredient for {@link #cookingRecipes} so it gets the right
     * recipe book category and set of cooking methods (smelting/smoking/campfire/blasting).
     */
    public enum CookingKind { FOOD, ORE, GENERIC }


    /**
     * Registers smelting plus, depending on kind, smoking/campfire (FOOD) or
     * blasting (ORE) recipes turning one ingredient into one result.
     */
    protected void cookingRecipes(RecipeOutput output,
                                  CookingKind category,
                                  String group,
                                  ItemLike ingredient,
                                  ItemLike result,
                                  float exp,
                                  int smeltingTime,
                                  String RecipeFileName,
                                  String criterionName,
                                  Criterion<?> criterion) {
        Ingredient input = Ingredient.of(ingredient);

        RecipeCategory recipeCategory = (category == CookingKind.FOOD) ? RecipeCategory.FOOD : RecipeCategory.MISC;
        CookingBookCategory bookCategory = (category == CookingKind.FOOD) ? CookingBookCategory.FOOD : CookingBookCategory.BLOCKS;

        SimpleCookingRecipeBuilder.smelting(input, recipeCategory, bookCategory, result, exp, smeltingTime)
                .unlockedBy(criterionName, criterion)
                .group(group)
                .save(output, MAF.MOD_ID + ":" + RecipeFileName + "_from_smelting");

        if (category == CookingKind.FOOD) {
            SimpleCookingRecipeBuilder.smoking(input, recipeCategory, result, exp, smeltingTime / 2)
                    .unlockedBy(criterionName, criterion)
                    .group(group)
                    .save(output, MAF.MOD_ID + ":" + RecipeFileName + "_from_smoking");

            SimpleCookingRecipeBuilder.campfireCooking(input, recipeCategory, result, exp, smeltingTime * 3)
                    .unlockedBy(criterionName, criterion)
                    .group(group)
                    .save(output, MAF.MOD_ID + ":" + RecipeFileName + "_from_campfire_cooking");
        } else if (category == CookingKind.ORE) {
            SimpleCookingRecipeBuilder.blasting(input, recipeCategory, bookCategory, result, exp, (smeltingTime / 2))
                    .unlockedBy(criterionName, criterion)
                    .group(group)
                    .save(output, MAF.MOD_ID + ":" + RecipeFileName + "_from_blasting");
        }
        //no case for GENERIC
    }

}
