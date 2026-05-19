package com.futurelin.occultism_ritualmaster.registry;

import com.futurelin.occultism_ritualmaster.OccultismRitualmaster;
import com.futurelin.occultism_ritualmaster.common.entity.RitualmasterEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class OrmEntitiesRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, OccultismRitualmaster.MOD_ID);

    public static final Supplier<EntityType<RitualmasterEntity>> RITUALMASTER = ENTITY_TYPES.register("ritualmaster",
            () -> EntityType.Builder.of(RitualmasterEntity::new, MobCategory.CREATURE)
                    .sized(0.8f, 1.875f)
                    .clientTrackingRange(8)
                    .build("ritualmaster"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
