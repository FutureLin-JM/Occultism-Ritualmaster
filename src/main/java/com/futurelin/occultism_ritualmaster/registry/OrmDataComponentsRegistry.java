package com.futurelin.occultism_ritualmaster.registry;

import com.futurelin.occultism_ritualmaster.OccultismRitualmaster;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class OrmDataComponentsRegistry {

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, OccultismRitualmaster.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ResourceLocation>>> SEALED_PENTACLE =
            COMPONENTS.register("sealed_pentacle", () ->
                    DataComponentType.<List<ResourceLocation>>builder()
                            .persistent(ResourceLocation.CODEC.listOf())
                            .networkSynchronized(ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()))
                            .build()
            );

    public static void register(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
    }
}
