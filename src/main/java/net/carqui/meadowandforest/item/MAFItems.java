package net.carqui.meadowandforest.item;

import net.carqui.meadowandforest.MAF;
import net.carqui.meadowandforest.block.MAFBlocks;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MAFItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MAF.MOD_ID);


    public static final DeferredItem<Item> TOMATO_SEEDS = ITEMS.registerItem("tomato_seeds",
            properties -> new BlockItem(MAFBlocks.TOMATO_CROP.get(), properties));

    public static final DeferredItem<Item> TOMATO = ITEMS.registerSimpleItem("tomato",
            props -> props.food(new FoodProperties.Builder()
                    .nutrition(2)
                    .saturationModifier(0.3f)
                    .build()));
    public static final DeferredItem<Item> VINE_TOMATO = ITEMS.registerSimpleItem("vine_tomato",
            props -> props.food(new FoodProperties.Builder()
                    .nutrition(1)
                    .saturationModifier(0.4f)
                    .build()));
    public static final DeferredItem<Item> CANE = ITEMS.registerSimpleItem("cane");
    public static final DeferredItem<Item> TOMATO_SOUP = ITEMS.registerSimpleItem("tomato_soup",
            props -> props.food(new FoodProperties.Builder()
                    .nutrition(6)
                    .saturationModifier(0.7f)
                    .build()));
    public static final DeferredItem<Item> HALF_BREAD = ITEMS.registerSimpleItem("half_a_bread",
            props -> props.food(new FoodProperties.Builder()
                    .nutrition(3)
                    .saturationModifier(0.4f)
                    .build()));
    public static final DeferredItem<Item> WHOLE_BREAD = ITEMS.registerSimpleItem("whole_bread",
            props -> props.food(new FoodProperties.Builder()
                    .nutrition(8)
                    .saturationModifier(0.6f)
                    .build()));
    public static final DeferredItem<Item> SLICE_OF_BREAD = ITEMS.registerSimpleItem("slice_of_bread",
            props -> props.food(new FoodProperties.Builder()
                    .nutrition(1)
                    .saturationModifier(0.3f)
                    .build()));
    public static final DeferredItem<Item> PA_AMB_TOMATA = ITEMS.registerSimpleItem("pa_amb_tomata",
            props -> props.food(new FoodProperties.Builder()
                    .nutrition(2)
                    .saturationModifier(0.6f)
                    .build()));
    public static final DeferredItem<Item> XAVA_ABERRATION = ITEMS.registerSimpleItem("xava_aberration",
            props -> props.food(
                    new FoodProperties.Builder()
                            .nutrition(2)
                            .saturationModifier(0.0f)
                            .build(),
                    Consumables.defaultFood()
                            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.HUNGER, 200, 0), 0.8f))
                            .build()));
    public static final DeferredItem<Item> BREADDOUGH = ITEMS.registerSimpleItem("breaddough");
    public static final DeferredItem<Item> SMALL_BREAD_BREADDOUGH = ITEMS.registerSimpleItem("small_bread_breaddough");
    public static final DeferredItem<Item> BIG_BREAD_BREADDOUGH = ITEMS.registerSimpleItem("big_bread_breaddough");
    public static final DeferredItem<Item> BAGUETTE_BREAD_BREADDOUGH = ITEMS.registerSimpleItem("baguette_bread_breaddough");

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
