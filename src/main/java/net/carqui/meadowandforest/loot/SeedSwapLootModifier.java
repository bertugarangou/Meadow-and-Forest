package net.carqui.meadowandforest.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

/**
 * A global loot modifier that replaces one item with another anywhere it appears
 * in generated loot, used to make grass sometimes drop tomato seeds instead of wheat seeds.
 */
public class SeedSwapLootModifier extends LootModifier {
    public static final MapCodec<SeedSwapLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecStart(inst).and(inst.group(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("from").forGetter(m -> m.from),
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("to").forGetter(m -> m.to)
            )).apply(inst, SeedSwapLootModifier::new)
    );

    private final Item from;
    private final Item to;

    /**
     * Creates the modifier with its conditions/priority plus the item to replace and its replacement.
     */
    public SeedSwapLootModifier(LootItemCondition[] conditions, int priority, Item from, Item to) {
        super(conditions, priority);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the codec used to serialize/deserialize this modifier for datapacks.
     */
    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    /**
     * Replaces every occurrence of the configured "from" item in the generated
     * loot with the configured "to" item, keeping the same stack count.
     */
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for (int i = 0; i < generatedLoot.size(); i++) {
            ItemStack stack = generatedLoot.get(i);
            if (stack.is(from)) {
                generatedLoot.set(i, new ItemStack(to, stack.getCount()));
            }
        }
        return generatedLoot;
    }
}
