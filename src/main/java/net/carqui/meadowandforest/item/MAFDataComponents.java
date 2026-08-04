package net.carqui.meadowandforest.item;

import com.mojang.serialization.Codec;
import net.carqui.meadowandforest.MAF;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MAFDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MAF.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FIREFLY_INSIDE_MARKER =
            DATA_COMPONENTS.register("firefly_inside_marker", () ->
                    DataComponentType.<Boolean>builder()
                            .persistent(Codec.BOOL)
                            .networkSynchronized(ByteBufCodecs.BOOL)
                            .build());

    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }
}