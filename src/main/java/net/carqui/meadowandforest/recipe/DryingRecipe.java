package net.carqui.meadowandforest.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.carqui.meadowandforest.MAF;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

/**
 * A drying-tray recipe: a single ingredient turns into a single result after
 * drying. Duration is NOT stored here - every drying recipe takes the same
 * fixed amount of time (see MAFDryingTrayBlockEntity#DRYING_TIME_TICKS).
 * <p>
 * This class is intentionally self-contained: it also holds the RecipeType,
 * MapCodec, StreamCodec and RecipeSerializer registration, so no separate
 * "MAFRecipeTypes"/"MAFRecipeSerializers" classes are needed.
 */
public class DryingRecipe implements Recipe<SingleRecipeInput> {

    private final Ingredient ingredient;
    private final ItemStackTemplate result;

    public DryingRecipe(Ingredient ingredient, ItemStackTemplate result) {
        this.ingredient = ingredient;
        this.result = result;
    }

    public Ingredient ingredient() {
        return this.ingredient;
    }

    public ItemStackTemplate result() {
        return this.result;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.ingredient.test(input.item());
    }

    @Override
    public @NonNull ItemStack assemble(@NonNull SingleRecipeInput input) {
        return this.result.create();
    }

    // No crafting-grid style recipe book presence: the drying tray has no menu/GUI,
    // so there is nothing meaningful for a recipe book to show for this recipe.
    @Override
    public boolean isSpecial() {
        return true;
    }

    // isSpecial() already hides this from the recipe book; no toast to show either way.
    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public @NonNull String group() {
        return "";
    }

    @Override
    public @NonNull RecipeBookCategory recipeBookCategory() {
        // isSpecial() is true, so this is never actually shown in a recipe book -
        // any concrete value satisfies the interface.
        return RecipeBookCategories.CRAFTING_MISC;
    }

    private PlacementInfo placementInfo;

    @Override
    public @NonNull PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(this.ingredient);
        }
        return this.placementInfo;
    }

    @Override
    public @NonNull RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return TYPE.get();
    }

    @Override
    public @NonNull RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return SERIALIZER.get();
    }

    public static final MapCodec<DryingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(DryingRecipe::ingredient),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(DryingRecipe::result)
    ).apply(instance, DryingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DryingRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, DryingRecipe::ingredient,
            ItemStackTemplate.STREAM_CODEC, DryingRecipe::result,
            DryingRecipe::new
    );

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, MAF.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, MAF.MOD_ID);

    public static final Supplier<RecipeType<DryingRecipe>> TYPE =
            RECIPE_TYPES.register("drying", RecipeType::simple);

    public static final Supplier<RecipeSerializer<DryingRecipe>> SERIALIZER =
            RECIPE_SERIALIZERS.register("drying", () -> new RecipeSerializer<>(CODEC, STREAM_CODEC));

    /**
     * Registers both deferred registers to the mod event bus. Call once from the main mod class.
     */
    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
    }
}
