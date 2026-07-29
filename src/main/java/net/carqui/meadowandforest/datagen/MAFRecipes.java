package net.carqui.meadowandforest.datagen;

import net.carqui.meadowandforest.MAF;
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
import net.neoforged.neoforge.common.conditions.NeoForgeConditions;

import java.util.concurrent.CompletableFuture;

public class MAFRecipes extends RecipeProvider {
    protected MAFRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries){
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new MAFRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Meadow and Forest Recipes";
        }
    }

    @Override
    protected void buildRecipes() {

        shapeless(RecipeCategory.FOOD, MAFItems.TOMATO_SEEDS.get())
                .requires(MAFItems.TOMATO)
                .unlockedBy(getHasName(MAFItems.TOMATO_SEEDS.get()), has(MAFItems.TOMATO_SEEDS))
                .group("tomato_seeds")
                .save(output /*, "meadowandforest:tomato_seeds"*/);

        shaped(RecipeCategory.FOOD, Items.BREAD)
                .pattern("WWW")
                .define('W', Items.WHEAT)
                .showNotification(false)
                .unlockedBy("never", has(Items.WHEAT))
                .save(output.withConditions(NeoForgeConditions.never()));

        cookingRecipes(output, CookingKind.FOOD, "bread", MAFItems.BREAD_DOUGH.get(), Items.BREAD, 0.07F, 75, "bread_dough", "bread", this.has(MAFItems.BREAD_DOUGH.get()));





    }


    public enum CookingKind {
        FOOD,
        ORE
    }


    protected void cookingRecipes(RecipeOutput output,
                                  CookingKind category,
                                  String group,
                                  ItemLike ingredient,
                                  ItemLike result,
                                  float exp,
                                  int smeltingTime,
                                  String recipeIdBase,
                                  String criterionName,
                                  Criterion<?> criterion) {

        Ingredient input = Ingredient.of(ingredient);

        RecipeCategory recipeCategory = (category == CookingKind.FOOD) ? RecipeCategory.FOOD : RecipeCategory.MISC;
        CookingBookCategory bookCategory = (category == CookingKind.FOOD) ? CookingBookCategory.FOOD : CookingBookCategory.BLOCKS;

        SimpleCookingRecipeBuilder.smelting(input, recipeCategory, bookCategory, result, exp, smeltingTime)
                .unlockedBy(criterionName, criterion)
                .group(group)
                .save(output, MAF.MOD_ID + ":" + recipeIdBase + "_from_smelting");

        if (category == CookingKind.FOOD) {
            SimpleCookingRecipeBuilder.smoking(input, recipeCategory, result, exp, smeltingTime / 2)
                    .unlockedBy(criterionName, criterion)
                    .group(group)
                    .save(output, MAF.MOD_ID + ":" + recipeIdBase + "_from_smoking");

            SimpleCookingRecipeBuilder.campfireCooking(input, recipeCategory, result, exp, smeltingTime * 3)
                    .unlockedBy(criterionName, criterion)
                    .group(group)
                    .save(output, MAF.MOD_ID + ":" + recipeIdBase + "_from_campfire_cooking");
        } else {
            SimpleCookingRecipeBuilder.blasting(input, recipeCategory, bookCategory, result, exp, (smeltingTime / 2))
                    .unlockedBy(criterionName, criterion)
                    .group(group)
                    .save(output, MAF.MOD_ID + ":" + recipeIdBase + "_from_blasting");
        }
    }





}
