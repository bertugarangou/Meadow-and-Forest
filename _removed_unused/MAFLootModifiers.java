package net.carqui.meadowandforest.loot;

import com.mojang.serialization.MapCodec;
import net.carqui.meadowandforest.MAF;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class MAFLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MAF.MOD_ID);

    public static final Supplier<MapCodec<SeedSwapLootModifier>> SEED_SWAP =
            GLOBAL_LOOT_MODIFIER_SERIALIZERS.register("seed_swap", () -> SeedSwapLootModifier.CODEC);

    public static void register(IEventBus eventBus) {
        GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(eventBus);
    }
}
