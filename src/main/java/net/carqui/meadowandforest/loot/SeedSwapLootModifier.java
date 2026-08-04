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
//TODO: wheat seeds stopped generating, it's a 100% tomato.
public class SeedSwapLootModifier extends LootModifier {
    public static final MapCodec<SeedSwapLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecStart(inst).and(inst.group(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("from").forGetter(m -> m.from),
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("to").forGetter(m -> m.to)
            )).apply(inst, SeedSwapLootModifier::new)
    );

    private final Item from;
    private final Item to;

    public SeedSwapLootModifier(LootItemCondition[] conditions, int priority, Item from, Item to) {
        super(conditions, priority);
        this.from = from;
        this.to = to;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

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
